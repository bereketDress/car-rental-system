package com.crms.dto.rental.check;

import com.crms.dto.rental.RentalResponse;

public record CheckInPaymentResponse(
        RentalResponse rental,
        String paymentMethod,
        String paymentStatus,
        boolean requiresCardPayment,
        String publishableKey,
        String clientSecret,
        String paymentIntentId,
        Float amount,
        Long paymentId
) {}
