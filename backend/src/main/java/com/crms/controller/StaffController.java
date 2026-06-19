package com.crms.controller;

import com.crms.dto.staff.StaffRequest;
import com.crms.dto.staff.StaffResponse;
import com.crms.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    public ResponseEntity<List<StaffResponse>> listAll() {
        return ResponseEntity.ok(staffService.getAll());
    }

    @PostMapping
    public ResponseEntity<StaffResponse> create(@RequestBody StaffRequest request) {
        return ResponseEntity.ok(staffService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffResponse> update(@PathVariable Long id, @RequestBody StaffRequest request) {
        return ResponseEntity.ok(staffService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        staffService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
