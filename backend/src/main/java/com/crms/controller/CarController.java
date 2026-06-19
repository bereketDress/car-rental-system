package com.crms.controller;

import com.crms.dto.car.CarRequest;
import com.crms.dto.car.CarResponse;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<CarResponse> add(@RequestBody CarRequest request) {
        return ResponseEntity.ok(carService.addCar(request));
    }

    @PutMapping("/{carId}")
    public ResponseEntity<CarResponse> update(@PathVariable Long carId, @RequestBody CarRequest request) {
        return ResponseEntity.ok(carService.updateCar(carId, request));
    }

    @DeleteMapping("/{carId}")
    public ResponseEntity<Void> delete(@PathVariable Long carId) {
        carService.deleteCar(carId);
        return ResponseEntity.noContent().build();
    }
}
