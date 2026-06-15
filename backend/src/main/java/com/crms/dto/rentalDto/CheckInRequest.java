package com.crms.dto.rentalDto;


import jakarta.validation.constraints.NotNull;

public record CheckInRequest(

        @NotNull Long rentalId,
        @NotNull Integer endMileage

) {}
