package com.crms.dto.car;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CarRequest(

        @NotBlank String plateNumber,
        @NotBlank String brand,
        @NotBlank String model,

        @NotNull Integer year,
        @NotNull Integer mileage,
        @NotBlank String availability,

        @NotNull Float dailyRate,
        @NotBlank String carType

) {}


