package com.crms.dto.customerDTO;

import com.crms.dto.rentalDto.RentalResponse;
import com.crms.dto.reservationDto.ReservationResponse;

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