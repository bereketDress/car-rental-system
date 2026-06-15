package com.crms.controller;

import com.crms.dto.CarDTO.CarResponse;
import lombok.RequiredArgsConstructor;
import com.crms.model.Car;
import com.crms.service.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping("/search")
    public ResponseEntity<List<CarResponse>> search(@RequestParam(required = false) String type,
                                                    @RequestParam(required = false) String carType) {
        return ResponseEntity.ok(carService.searchAvailableResponses(type != null ? type : carType));
    }

    @GetMapping
    public ResponseEntity<List<CarResponse>> listAll() {
        return ResponseEntity.ok(carService.listAllResponses());
    }

    @PostMapping
    public ResponseEntity<Car> add(@RequestBody Car car) {
        return ResponseEntity.ok(carService.addCar(car));
    }

    @PutMapping("/{vinNumber}")
    public ResponseEntity<Car> update(@PathVariable String vinNumber, @RequestBody Car car) {
        return ResponseEntity.ok(carService.updateCar(vinNumber, car));
    }

    @DeleteMapping("/{vinNumber}")
    public ResponseEntity<Void> delete(@PathVariable String vinNumber) {
        carService.deleteCar(vinNumber);
        return ResponseEntity.noContent().build();
    }
}
