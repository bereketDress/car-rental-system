package com.crms.controller;

import com.crms.model.Car;
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
    public ResponseEntity<List<Car>> getInventory(@PathVariable Long branchId) {
        return ResponseEntity.ok(managerService.getInventory(branchId));
    }

    @PostMapping("/branches/{branchId}/vehicles")
    public ResponseEntity<Car> addVehicle(@PathVariable Long branchId, @RequestBody Car car) {
        return ResponseEntity.ok(managerService.addCarToInventory(branchId, car));
    }

    @PutMapping("/vehicles/{vinNumber}")
    public ResponseEntity<Car> updateVehicle(@PathVariable String vinNumber, @RequestBody Car car) {
        return ResponseEntity.ok(managerService.updateCarInInventory(vinNumber, car));
    }

    @DeleteMapping("/branches/{branchId}/vehicles/{vinNumber}")
    public ResponseEntity<Void> removeVehicle(@PathVariable Long branchId, @PathVariable String vinNumber) {
        managerService.removeCarFromInventory(branchId, vinNumber);
        return ResponseEntity.noContent().build();
    }
}
