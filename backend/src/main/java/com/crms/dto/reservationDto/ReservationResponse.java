package com.crms.dto.reservationDto;

import java.time.LocalDate;

public record ReservationResponse(
        Long reservationId,
        LocalDate reservationDate,
        LocalDate pickupDate,
        String status,
        CustomerSummary customer,
        StaffSummary staff,
        CarSummary car
) {
    public record CustomerSummary(
            Long customerId,
            String name,
            String email,
            String phone
    ) {}

    public record StaffSummary(
            Long staffId,
            String name,
            String email,
            String role
    ) {}

    public record CarSummary(
            String vinNumber,
            String plateNumber,
            String brand,
            String model,
            int year,
            double dailyRate,
            String carType
    ) {}
}
