package com.crms.dto.customer;

import com.crms.dto.rental.RentalResponse;
import com.crms.dto.reservation.ReservationResponse;

import java.util.List;

public record CustomerResponse(
        Long custId,
        String name,
        String phone,
        String licenseNumber,
        Float outstandingBalance,
        List<ReservationResponse> reservations,
        List<RentalResponse> rentals
) {}