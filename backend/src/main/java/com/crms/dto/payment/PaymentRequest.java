package com.crms.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record PaymentRequest(

        Long rentalId,
        @NotNull LocalDate paymentDate,
        @NotNull Float amount,
        @NotBlank String paymentMethod

) {}
