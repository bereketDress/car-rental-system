package com.crms.controller;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.crms.service.PaymentService;
import com.crms.service.StripeService;
import com.crms.config.StripeConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/stripe")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    private final StripeService stripeService;
    private final PaymentService paymentService;
    private final StripeConfig stripeConfig;

    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> config() {
        return ResponseEntity.ok(Map.of("publishableKey", stripeConfig.getPublishableKey()));
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            Event event = stripeService.constructWebhookEvent(payload, sigHeader);

            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = deserializer.getObject().orElse(null);

            switch (event.getType()) {
                case "payment_intent.succeeded" -> {
                    if (stripeObject instanceof PaymentIntent intent) {
                        paymentService.markPaid(intent.getId());
                        log.info("Payment succeeded: {}", intent.getId());
                    }
                }
                case "payment_intent.payment_failed" -> {
                    if (stripeObject instanceof PaymentIntent intent) {
                        paymentService.markFailed(intent.getId());
                        log.warn("Payment failed: {}", intent.getId());
                    }
                }
                default -> log.info("Unhandled Stripe event: {}", event.getType());
            }

            return ResponseEntity.ok("Received");

        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe webhook signature");
            return ResponseEntity.badRequest().body("Invalid signature");
        } catch (com.stripe.exception.StripeException e) {
            log.error("Stripe webhook processing failed", e);
            return ResponseEntity.status(500).body("Stripe processing error");
        }
    }
}
