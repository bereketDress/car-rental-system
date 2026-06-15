package com.crms.dto.damageDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record DamageRequest(

        @NotNull LocalDate reportDate,
        @NotNull Float repairCost,
        @NotBlank String status,
        @NotBlank String description

) {}
