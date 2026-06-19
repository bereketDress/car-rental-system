package com.crms.service;

import com.crms.dto.damage.DamageRequest;
import com.crms.dto.damage.DamageResponse;
import com.crms.exception.ResourceNotFoundException;
import com.crms.model.Damage;
import com.crms.repository.DamageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DamageService {

    private static final String DAMAGE_REPORTED = "REPORTED";

    private final DamageRepository damageRepository;

    public List<DamageResponse> getAll() {
        return damageRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DamageResponse getById(Long id) {
        Damage damage = getDamage(id);
        return toResponse(damage);
    }

    public DamageResponse create(DamageRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Damage cannot be empty");
        }

        Damage damage = new Damage();
        damage.setReportDate(request.reportDate());
        damage.setRepairCost(request.repairCost());
        damage.setDescription(request.description());
        damage.setStatus(DAMAGE_REPORTED);

        Damage savedDamage = damageRepository.save(damage);
        return toResponse(savedDamage);
    }

    public DamageResponse updateStatus(Long id, String newStatus) {
        Damage damage = getDamage(id);

        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("Status is required");
        }

        String status = newStatus.toUpperCase();

        List<String> validStatuses = List.of(
                "REPORTED",
                "UNDER_REPAIR",
                "REPAIRED",
                "CLOSED"
        );

        if (!validStatuses.contains(status)) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        damage.setStatus(status);

        Damage savedDamage = damageRepository.save(damage);
        return toResponse(savedDamage);
    }

    public boolean delete(Long id) {
        Damage damage = getDamage(id);
        damageRepository.delete(damage);
        return true;
    }

    private Damage getDamage(Long id) {
        return damageRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Damage not found: " + id));
    }

    private DamageResponse toResponse(Damage damage) {
        return new DamageResponse(
                damage.getDamageId(),
                damage.getReportDate(),
                damage.getRepairCost(),
                damage.getStatus(),
                damage.getDescription(),
                List.of()
        );
    }
}
