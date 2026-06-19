package com.crms.dto.rental.check;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CheckOutRequest(
        @NotNull Long reservationId,
        @NotNull Integer startMileage,
        @NotNull LocalDate returnDate
) {}
