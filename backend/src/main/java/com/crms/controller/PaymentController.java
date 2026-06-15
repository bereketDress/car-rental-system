package com.crms.controller;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import com.crms.config.StripeConfig;
import com.crms.model.Payment;
import com.crms.service.PaymentService;
import com.crms.service.StripeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

import static com.crms.util.EntityFields.longValue;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final StripeService stripeService;
    private final StripeConfig stripeConfig;

    @GetMapping
    public ResponseEntity<?> listAll(Authentication authentication) {
        Long customerId = authenticatedCustomerId(authentication);
        if (customerId != null) {
            return ResponseEntity.ok(paymentService.listByCustomer(customerId));
        }
        return ResponseEntity.ok(paymentService.getAll());
    }

    @PostMapping("/record")
    public ResponseEntity<Payment> record(@RequestBody Map<String, String> body, Authentication authentication) {
        return ResponseEntity.ok(paymentService.recordPayment(
                Long.parseLong(body.get("rentalId")),
                body.getOrDefault("paymentMethod", "CASH"),
                authenticatedCustomerId(authentication)
        ));
    }

    @PostMapping("/process")
    public ResponseEntity<Payment> process(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(paymentService.recordPayment(
                Long.parseLong(body.get("rentalId")),
                body.getOrDefault("paymentMethod", "CASH")
        ));
    }

    @PostMapping("/create-intent")
    public ResponseEntity<?> createIntent(@RequestBody Map<String, String> body, Authentication authentication) {
        try {
            Long rentalId = Long.parseLong(body.get("rentalId"));
            Long customerId = authenticatedCustomerId(authentication);
            paymentService.validateCanCreateCardPayment(rentalId, customerId);
            Float amount = paymentService.computeCharges(rentalId, customerId);
            PaymentIntent intent = stripeService.createPaymentIntent(amount);
            Payment payment = paymentService.createPendingPayment(rentalId, intent.getId(), customerId);
            return ResponseEntity.ok(Map.of(
                    "publishableKey", stripeConfig.getPublishableKey(),
                    "clientSecret", intent.getClientSecret(),
                    "paymentIntentId", intent.getId(),
                    "amount", amount,
                    "paymentId", longValue(payment, "paymentId")
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/confirm-card")
    public ResponseEntity<?> confirmCard(@RequestBody Map<String, String> body, Authentication authentication) {
        String paymentIntentId = body.get("paymentIntentId");
        try {
            PaymentIntent intent = stripeService.retrievePaymentIntent(paymentIntentId);
            if (!"succeeded".equals(intent.getStatus())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Card payment has not succeeded");
            }

            paymentService.markPaid(intent.getId(), authenticatedCustomerId(authentication));
            return ResponseEntity.ok(Map.of("status", "COMPLETED"));
        } catch (StripeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unable to confirm Stripe payment");
        }
    }

    @PostMapping("/record-card")
    public ResponseEntity<Map<String, String>> recordCard() {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Card payments must be completed with Stripe. Use /api/payments/create-intent, confirm with Stripe.js, then call /api/payments/confirm-card."
        );
    }

    private Long authenticatedCustomerId(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities().stream()
                .noneMatch(authority -> "ROLE_CUSTOMER".equals(authority.getAuthority()))) {
            return null;
        }

        Object details = authentication.getDetails();
        if (details instanceof Map<?, ?> map && map.get("userId") instanceof Long userId) {
            return userId;
        }

        return null;
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(@RequestBody Map<String, Object> body) {
        // Simple mock webhook handler
        String type = (String) body.get("type");
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        Map<String, Object> object = (Map<String, Object>) data.get("object");
        String intentId = (String) object.get("id");

        if ("payment_intent.succeeded".equals(type)) {
            paymentService.markPaid(intentId);
        } else if ("payment_intent.payment_failed".equals(type)) {
            paymentService.markFailed(intentId);
        }
        return ResponseEntity.ok().build();
    }
}
