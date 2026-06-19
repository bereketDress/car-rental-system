package com.crms.dto.rental;

import jakarta.validation.constraints.NotNull;

public record RentalRequest(

        @NotNull Long reservationId

) {}