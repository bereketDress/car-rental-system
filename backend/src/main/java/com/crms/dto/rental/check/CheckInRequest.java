package com.crms.dto.rental.check;

import jakarta.validation.constraints.NotNull;

public record CheckInRequest(
        @NotNull Long rentalId,
        @NotNull Integer endMileage,
        String damageDescription,
        Float repairCost,
        String paymentMethod
) {}
