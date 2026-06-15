package com.crms.dto.rentalDto;

import com.crms.model.Rental;

public record CheckInPaymentResponse(
        Rental rental,
        String paymentMethod,
        String paymentStatus,
        boolean requiresCardPayment,
        String publishableKey,
        String clientSecret,
        String paymentIntentId,
        Float amount,
        Long paymentId
) {}
