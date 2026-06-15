package com.crms.dto.staffdto;
import com.crms.dto.paymentDto.PaymentResponse;
import com.crms.dto.reservationDto.ReservationResponse;

import java.util.List;

public record StaffResponse(
        Long staffId,
        String name,
        String role,
        String email,
        String phone,
        List<PaymentResponse> payments,
        List<ReservationResponse> reservations
) {}
