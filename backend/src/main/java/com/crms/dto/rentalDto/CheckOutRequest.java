package com.crms.dto.rentalDto;


import jakarta.validation.constraints.NotNull;

public record CheckOutRequest(

        @NotNull Long rentalId,
        @NotNull Integer startMileage

) {}
