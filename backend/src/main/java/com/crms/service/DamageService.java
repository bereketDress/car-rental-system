package com.crms.service;
import com.crms.model.*;
import lombok.RequiredArgsConstructor;
import com.crms.repository.DamageRepository;
import org.springframework.stereotype.Service;
import java.util.List;

import static com.crms.util.EntityFields.*;

@Service
@RequiredArgsConstructor
public class DamageService {

    private final DamageRepository damageRepository;

    public List<Damage> getAll() { return damageRepository.findAll(); }

    public Damage getById(Long id) {
        return damageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Damage not found: " + id));
    }

    public Damage create(Damage damage) {
        set(damage, "status", DAMAGE_REPORTED);
        return damageRepository.save(damage);
    }


    // Valid status transitions: REPORTED -> UNDER_REPAIR -> REPAIRED -> CLOSED
    public Damage updateStatus(Long id, String newStatus) {
        Damage damage = getById(id);

        List<String> validStatuses = List.of("REPORTED", "UNDER_REPAIR", "REPAIRED", "CLOSED");

        if (!validStatuses.contains(newStatus.toUpperCase())) {
            throw new RuntimeException(
                    "Invalid status: " + newStatus + ". Valid values: " + validStatuses
            );
        }

        set(damage, "status", newStatus.toUpperCase());
        return damageRepository.save(damage);
    }

    public void delete(Long id) { damageRepository.deleteById(id); }
}
