package com.crms.dto.CarDTO;

public record CarResponse(
        Long carId,
        String vinNumber,
        String plateNumber,
        String brand,
        String model,
        Integer year,
        Integer mileage,
        Boolean availability,
        String availabilityStatus,
        Double dailyRate,
        String carType,
        Long branchId,
        String branchName
) {}
