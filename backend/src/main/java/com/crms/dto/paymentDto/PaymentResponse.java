package com.crms.dto.paymentDto;

import com.crms.dto.damageDto.DamageResponse;

import java.time.LocalDate;
import java.util.List;

public record PaymentResponse(
        Long paymentId,
        LocalDate paymentDate,
        Float amount,
        String paymentMethod,
        List<DamageResponse> damages
) {}
