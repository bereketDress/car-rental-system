package com.crms.dto.rentalDto;

import com.crms.dto.damageDto.DamageResponse;

import java.time.LocalDate;
import java.util.List;

public record RentalResponse(
        Long rentalId,
        LocalDate checkoutDate,
        LocalDate returnDate,
        Integer startMileage,
        Integer endMileage,
        String status,
        Float lateFee,
        List<DamageResponse> damages
) {}