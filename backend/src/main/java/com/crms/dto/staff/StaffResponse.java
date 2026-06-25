package com.crms.dto.staff;
import com.crms.dto.payment.PaymentResponse;
import com.crms.dto.reservation.ReservationResponse;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StaffResponse(
        Long staffId,
        String name,
        String role,
        String email,
        String phone,
        List<PaymentResponse> payments,
        List<ReservationResponse> reservations,
        String temporaryPassword
) {
    public StaffResponse(
            Long staffId,
            String name,
            String role,
            String email,
            String phone,
            List<PaymentResponse> payments,
            List<ReservationResponse> reservations
    ) {
        this(staffId, name, role, email, phone, payments, reservations, null);
    }
}
