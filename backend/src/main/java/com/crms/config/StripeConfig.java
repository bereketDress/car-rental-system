package com.crms.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class StripeConfig {

    @Value("${stripe.secret.key:sk_test_ReplaceMeWithYourStripeSecretKey}")
    private String secretKey;

    @Value("${stripe.publishable.key:pk_test_ReplaceMeWithYourStripePublishableKey}")
    private String publishableKey;

    @Value("${stripe.webhook.secret:whsec_ReplaceMeWithYourWebhookSecret}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        if (isPlaceholder(secretKey, "sk_") || isPlaceholder(publishableKey, "pk_")) {
            throw new IllegalStateException(
                    "Stripe keys are not configured. Set STRIPE_SECRET_KEY and STRIPE_PUBLISHABLE_KEY, or run with the local profile."
            );
        }

        Stripe.apiKey = secretKey;
    }

    private boolean isPlaceholder(String value, String expectedPrefix) {
        return value == null
                || value.isBlank()
                || !value.startsWith(expectedPrefix)
                || value.contains("ReplaceMe")
                || value.contains("xxxx");
    }
}
