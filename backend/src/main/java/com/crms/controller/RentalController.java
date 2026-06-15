package com.crms.controller;

import com.crms.dto.rentalDto.CheckInPaymentResponse;
import com.crms.config.StripeConfig;
import com.crms.model.Payment;
import com.crms.service.PaymentService;
import com.crms.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import com.crms.model.Rental;
import com.crms.service.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.crms.util.EntityFields.doubleValue;
import static com.crms.util.EntityFields.longValue;
import static com.crms.util.EntityFields.string;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;
    private final PaymentService paymentService;
    private final StripeService stripeService;
    private final StripeConfig stripeConfig;

    @GetMapping
    public ResponseEntity<List<Rental>> list(@RequestParam(required = false) Long customerId, Authentication authentication) {
        Long authenticatedCustomerId = authenticatedCustomerId(authentication);
        if (authenticatedCustomerId != null) {
            return ResponseEntity.ok(rentalService.listByCustomer(authenticatedCustomerId));
        }

        if (!hasStaffAccess(authentication)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login is required to view rentals");
        }

        if (customerId != null) {
            return ResponseEntity.ok(rentalService.listByCustomer(customerId));
        }
        return ResponseEntity.ok(rentalService.getAll());
    }

    @PostMapping("/checkout")
    public ResponseEntity<Rental> checkOut(@RequestBody Map<String, String> body, Authentication authentication) {
        return ResponseEntity.ok(rentalService.checkOut(
                Long.parseLong(body.get("reservationId")),
                parseMileage(body.get("startMileage")),
                LocalDate.parse(body.get("returnDate")),
                authenticatedCustomerId(authentication)
        ));
    }

    @PostMapping("/{id}/checkin")
    public ResponseEntity<CheckInPaymentResponse> checkIn(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication authentication) {
        String paymentMethod = normalizePaymentMethod(body);
        Long customerId = authenticatedCustomerId(authentication);
        String repairCost = body.get("repairCost");
        Rental rental = rentalService.checkIn(
                id,
                parseMileage(body.get("endMileage")),
                body.get("damageDescription"),
                repairCost != null && !repairCost.isBlank() ? Float.parseFloat(repairCost) : null,
                customerId
        );

        if ("CASH".equals(paymentMethod)) {
            return ResponseEntity.ok(cashResponse(rental, paymentService.recordPayment(id, "CASH", customerId)));
        }

        try {
            Float amount = paymentService.computeCharges(id, customerId);
            PaymentIntent intent = stripeService.createPaymentIntent(amount);
            Payment payment = paymentService.createPendingPayment(id, intent.getId(), customerId);
            return ResponseEntity.ok(cardResponse(rental, payment, intent, amount));
        } catch (StripeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unable to create Stripe payment intent");
        }
    }

    private CheckInPaymentResponse cashResponse(Rental rental, Payment payment) {
        return new CheckInPaymentResponse(
                rental, "CASH", string(payment, "status"), false,
                null, null, null, doubleValue(payment, "amount").floatValue(), longValue(payment, "paymentId")
        );
    }

    private CheckInPaymentResponse cardResponse(Rental rental, Payment payment, PaymentIntent intent, Float amount) {
        return new CheckInPaymentResponse(
                rental, "CARD", string(payment, "status"), true,
                stripeConfig.getPublishableKey(), intent.getClientSecret(), intent.getId(), amount, longValue(payment, "paymentId")
        );
    }

    private String normalizePaymentMethod(Map<String, String> body) {
        String raw = firstPresent(body, "paymentMethod", "paymentType", "method");
        String method = raw == null || raw.isBlank()
                ? "CASH"
                : raw.trim().toUpperCase(Locale.ROOT);

        if ("CASH".equals(method)) {
            return "CASH";
        }

        if ("CARD".equals(method) || "CREDIT_CARD".equals(method) || "STRIPE".equals(method)) {
            return "CARD";
        }

        throw new IllegalArgumentException("paymentMethod must be CASH or CARD");
    }

    private String firstPresent(Map<String, String> body, String... keys) {
        for (String key : keys) {
            String value = body.get(key);
            if (value != null) {
                return value;
            }
        }

        return null;
    }

    private Integer parseMileage(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Mileage is required");
        }

        return (int) Math.round(Double.parseDouble(value));
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

    private boolean hasStaffAccess(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_STAFF".equals(authority.getAuthority())
                        || "ROLE_MANAGER".equals(authority.getAuthority()));
    }
}
