package com.crms.dto.reservation;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReservationRequest(

        LocalDate reservationDate,
        @NotNull LocalDate pickupDate,
        String status,

        Long customerId,
        @NotNull Long carId

) {}
