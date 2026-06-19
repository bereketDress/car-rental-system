package com.crms.controller;

import com.crms.dto.damage.DamageResponse;
import com.crms.service.DamageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/damages")
@RequiredArgsConstructor
public class DamageController {

    private final DamageService damageService;

    @GetMapping
    public ResponseEntity<List<DamageResponse>> listAll() {
        return ResponseEntity.ok(damageService.getAll());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DamageResponse> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(damageService.updateStatus(id, body.get("status")));
    }
}
