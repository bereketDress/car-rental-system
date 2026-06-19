package com.crms.controller;

import com.crms.config.StripeConfig;
import com.crms.dto.payment.PaymentRequest;
import com.crms.dto.payment.PaymentResponse;
import com.crms.service.PaymentService;
import com.crms.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final StripeService stripeService;
    private final StripeConfig stripeConfig;

    @GetMapping
    public List<PaymentResponse> all(Authentication auth) {
        Long customerId = authenticatedCustomerId(auth);
        return customerId == null
                ? paymentService.getAll()
                : paymentService.listByCustomer(customerId);
    }

    @PostMapping("/record")
    public PaymentResponse record(@RequestBody Map<String, String> body, Authentication auth) {
        return paymentService.recordPayment(
                paymentRequest(body),
                authenticatedCustomerId(auth)
        );
    }

    @PostMapping("/process")
    public PaymentResponse process(@RequestBody Map<String, String> body) {
        return paymentService.recordPayment(
                paymentRequest(body),
                null
        );
    }

    @PostMapping("/create-intent")
    public Map<String, Object> createIntent(@RequestBody Map<String, String> body, Authentication auth) {
        try {
            Long rentalId = Long.parseLong(body.get("rentalId"));
            Long customerId = authenticatedCustomerId(auth);
            paymentService.validateCanCreateCardPayment(rentalId, customerId);
            Float amount = paymentService.computeCharges(rentalId, customerId).amount();
            PaymentIntent intent = stripeService.createPaymentIntent(amount);
            PaymentResponse payment = paymentService.createPendingPayment(rentalId, intent.getId(), customerId);

            return Map.of(
                    "publishableKey", stripeConfig.getPublishableKey(),
                    "clientSecret", intent.getClientSecret(),
                    "paymentIntentId", intent.getId(),
                    "amount", amount,
                    "paymentId", payment.paymentId()
            );
        } catch (StripeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unable to create Stripe payment intent");
        }
    }

    @PostMapping("/confirm-card")
    public Map<String, String> confirmCard(@RequestBody Map<String, String> body, Authentication auth) {
        String paymentIntentId = body.get("paymentIntentId");
        try {
            PaymentIntent intent = stripeService.retrievePaymentIntent(paymentIntentId);
            if (!"succeeded".equals(intent.getStatus())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Card payment has not succeeded");
            }

            paymentService.markPaid(intent.getId(), authenticatedCustomerId(auth));
            return Map.of("status", "COMPLETED");
        } catch (StripeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unable to confirm Stripe payment");
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody Map<String, Object> body) {
        String type = (String) body.get("type");
        Object dataObject = body.get("data");
        if (dataObject instanceof Map<?, ?> data && data.get("object") instanceof Map<?, ?> object) {
            Object intentId = object.get("id");
            if (intentId instanceof String id) {
                if ("payment_intent.succeeded".equals(type)) {
                    paymentService.markPaid(id);
                } else if ("payment_intent.payment_failed".equals(type)) {
                    paymentService.markFailed(id);
                }
            }
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/record-card")
    public ResponseEntity<Map<String, String>> recordCard() {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Card payments must be completed with Stripe."
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

    private PaymentRequest paymentRequest(Map<String, String> body) {
        return new PaymentRequest(
                Long.parseLong(body.get("rentalId")),
                null,
                null,
                body.getOrDefault("paymentMethod", "CASH")
        );
    }
}
