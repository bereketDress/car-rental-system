package com.crms.dto.staff;
import com.crms.dto.payment.PaymentResponse;
import com.crms.dto.reservation.ReservationResponse;

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
