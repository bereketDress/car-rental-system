package com.crms.controller;

import com.crms.dto.car.CarRequest;
import com.crms.dto.car.CarResponse;
import com.crms.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @GetMapping("/reports")
    public ResponseEntity<Map<String, Object>> viewReports(@RequestParam(required = false) Long branchId) {
        return ResponseEntity.ok(managerService.viewReports(branchId));
    }

    @GetMapping("/branches/{branchId}/vehicles")
    public ResponseEntity<List<CarResponse>> getInventory(@PathVariable Long branchId) {
        return ResponseEntity.ok(managerService.getInventory(branchId));
    }

    @PostMapping("/branches/{branchId}/vehicles")
    public ResponseEntity<CarResponse> addVehicle(@PathVariable Long branchId, @RequestBody CarRequest request) {
        return ResponseEntity.ok(managerService.addCarToInventory(branchId, request));
    }

    @PutMapping("/vehicles/{carId}")
    public ResponseEntity<CarResponse> updateVehicle(@PathVariable Long carId, @RequestBody CarRequest request) {
        return ResponseEntity.ok(managerService.updateCarInInventory(carId, request));
    }

    @DeleteMapping("/branches/{branchId}/vehicles/{carId}")
    public ResponseEntity<Void> removeVehicle(@PathVariable Long branchId, @PathVariable Long carId) {
        managerService.removeCarFromInventory(branchId, carId);
        return ResponseEntity.noContent().build();
    }
}
