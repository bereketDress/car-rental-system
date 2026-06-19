package com.crms.dto.rental;

import java.time.LocalDate;

public record RentalResponse(
        Long rentalId,
        LocalDate checkoutDate,
        LocalDate returnDate,
        Integer startMileage,
        Integer endMileage,
        String status,
        Float baseCharge,
        Float damageRepairCost,
        Float totalCharge,
        CarSummary car,
        CustomerSummary customer
) {
    public record CarSummary(
            Long carId,
            String brand,
            String model,
            String plateNumber,
            String carType
    ) {}

    public record CustomerSummary(
            Long customerId,
            String name
    ) {}
}
