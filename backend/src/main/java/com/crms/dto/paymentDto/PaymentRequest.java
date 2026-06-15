package com.crms.dto.paymentDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record PaymentRequest(

        @NotNull LocalDate paymentDate,
        @NotNull Float amount,
        @NotBlank String paymentMethod

) {}
