package com.crms.dto.payment;

import java.time.LocalDate;

public record PaymentResponse(
        Long paymentId,
        LocalDate paymentDate,
        Float amount,
        String paymentMethod,
        String status,
        String stripePaymentIntentId,
        Long rentalId
) {}
