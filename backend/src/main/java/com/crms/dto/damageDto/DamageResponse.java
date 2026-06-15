package com.crms.dto.damageDto;

import com.crms.dto.paymentDto.PaymentResponse;

import java.time.LocalDate;
import java.util.List;

public record DamageResponse(
        Long damageId,
        LocalDate reportDate,
        Float repairCost,
        String status,
        String description,
        List<PaymentResponse> payments
) {}
