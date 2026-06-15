package com.crms.service;
import com.crms.dto.CarDTO.CarResponse;
import com.crms.model.Branch;
import lombok.RequiredArgsConstructor;
import com.crms.model.Car;
import com.crms.repository.CarRepository;
import org.springframework.stereotype.Service;
import java.util.List;

import static com.crms.util.EntityFields.*;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    public List<Car> listAll() { return carRepository.findAll(); }

    public List<CarResponse> listAllResponses() {
        return listAll().stream().map(this::toResponse).toList();
    }

    public Car getCar(String vinNumber) {
        return carRepository.findByVinNumber(vinNumber)
                .orElseThrow(() -> new RuntimeException("Car not found: " + vinNumber));
    }

    public boolean isAvailable(String vinNumber) {
        Car car = getCar(vinNumber);
        return available(car);
    }

    public List<Car> searchAvailable(String carType) {
        return carRepository.findAll().stream()
                .filter(car -> available(car))
                .filter(c -> carType == null || carType.isBlank()
                        || carType.equalsIgnoreCase(string(c, "carType")))
                .toList();
    }

    public List<CarResponse> searchAvailableResponses(String carType) {
        return searchAvailable(carType).stream().map(this::toResponse).toList();
    }

    public Car addCar(Car car) {
        setAvailable(car, true);
        return carRepository.save(car);
    }

    public Car updateAvailability(String vinNumber, boolean availability) {
        Car car = getCar(vinNumber);
        setAvailable(car, availability);
        return carRepository.save(car);
    }

    public Car updateCar(String vinNumber, Car updated) {
        Car existing = getCar(vinNumber);
        set(existing, "brand", string(updated, "brand"));
        set(existing, "model", string(updated, "model"));
        set(existing, "plateNumber", string(updated, "plateNumber"));
        set(existing, "year", integer(updated, "year"));
        set(existing, "mileage", integer(updated, "mileage"));
        set(existing, "availability", string(updated, "availability"));
        set(existing, "dailyRate", doubleValue(updated, "dailyRate"));
        set(existing, "carType", string(updated, "carType"));
        set(existing, "branch", get(updated, "branch"));
        return carRepository.save(existing);
    }

    public boolean deleteCar(String vinNumber) {
        carRepository.delete(getCar(vinNumber));
        return true;
    }

    public CarResponse toResponse(Car car) {
        Branch branch = get(car, "branch", Branch.class);
        return new CarResponse(
                longValue(car, "carId"),
                string(car, "vinNumber"),
                string(car, "plateNumber"),
                string(car, "brand"),
                string(car, "model"),
                integer(car, "year"),
                integer(car, "mileage"),
                available(car),
                string(car, "availability"),
                doubleValue(car, "dailyRate"),
                string(car, "carType"),
                branch == null ? null : branch.getBranchId(),
                branch == null ? null : branch.getName()
        );
    }
}
