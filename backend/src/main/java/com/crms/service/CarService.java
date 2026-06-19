package com.crms.service;

import com.crms.dto.car.CarRequest;
import com.crms.dto.car.CarResponse;
import com.crms.model.Car;
import com.crms.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    public List<CarResponse> listAll() {
        return carRepository.findAll()
                .stream()
                .map(this::carResponse)
                .toList();
    }

    public List<CarResponse> listAllResponses() {
        return listAll();
    }

    public List<CarResponse> searchAvailableResponses(String carType) {
        return carRepository.findAll().stream()
                .filter(this::available)
                .filter(car -> carType == null || carType.isBlank()
                        || carType.equalsIgnoreCase(car.getCarType()))
                .map(this::carResponse)
                .toList();
    }

    public CarResponse addCar(CarRequest request) {
        Car car = new Car();

        car.setPlateNumber(request.plateNumber());
        car.setBrand(request.brand());
        car.setModel(request.model());
        car.setYear(request.year());
        car.setMileage(request.mileage());
        car.setDailyRate(request.dailyRate().doubleValue());
        car.setCarType(request.carType());
        car.setAvailability("AVAILABLE");

        return carResponse(carRepository.save(car));
    }

    public CarResponse updateCar(Long id, CarRequest request) {
        Car car = getCarEntity(id);

        car.setPlateNumber(request.plateNumber());
        car.setBrand(request.brand());
        car.setModel(request.model());
        car.setYear(request.year());
        car.setMileage(request.mileage());
        car.setDailyRate(request.dailyRate().doubleValue());
        car.setCarType(request.carType());
        car.setAvailability(request.availability());

        return carResponse(carRepository.save(car));
    }

    public CarResponse updateAvailability(Long id, boolean available) {
        Car car = getCarEntity(id);

        car.setAvailability(available ? "AVAILABLE" : "UNAVAILABLE");

        return carResponse(carRepository.save(car));
    }

    public boolean deleteCar(Long id) {
        carRepository.deleteById(id);
        return true;
    }

    Car getCarEntity(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found: " + id));
    }

    private boolean available(Car car) {
        return "AVAILABLE".equalsIgnoreCase(car.getAvailability())
                || "TRUE".equalsIgnoreCase(car.getAvailability());
    }

    private CarResponse carResponse(Car car) {
        return new CarResponse(
                car.getCarId(),
                car.getPlateNumber(),
                car.getBrand(),
                car.getModel(),
                car.getYear(),
                car.getMileage(),
                available(car),
                car.getAvailability(),
                car.getDailyRate(),
                car.getCarType(),
                null,
                null
        );
    }
}
