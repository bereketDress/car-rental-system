package com.crms.dto.CarDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CarRequest(

        @NotBlank String vinNumber,
        @NotBlank String plateNumber,
        @NotBlank String brand,
        @NotBlank String model,

        @NotNull Integer year,
        @NotNull Integer mileage,
        @NotBlank String availability,

        @NotNull Float dailyRate,
        @NotBlank String carType

) {}


