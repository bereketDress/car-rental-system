package com.crms.dto.rentalDto;

import jakarta.validation.constraints.NotNull;

public record RentalRequest(

        @NotNull Long reservationId

) {}