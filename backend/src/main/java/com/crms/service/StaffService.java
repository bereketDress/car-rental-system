package com.crms.service;

import com.crms.dto.staff.StaffRequest;
import com.crms.dto.staff.StaffResponse;
import com.crms.model.Staff;
import com.crms.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    public List<StaffResponse> getAll() {
        return staffRepository.findAll().stream()
                .filter(staff -> "STAFF".equalsIgnoreCase(staff.getRole()))
                .map(staff -> new StaffResponse(
                        staff.getStaffId(),
                        staff.getName(),
                        staff.getRole(),
                        staff.getEmail(),
                        staff.getPhone(),
                        Collections.emptyList(),
                        Collections.emptyList()
                ))
                .toList();
    }

    public StaffResponse getById(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found: " + id));

        return new StaffResponse(
                staff.getStaffId(),
                staff.getName(),
                staff.getRole(),
                staff.getEmail(),
                staff.getPhone(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    public StaffResponse create(StaffRequest request) {
        Staff staff = new Staff();
        staff.setName(request.name());
        staff.setEmail(request.email());
        staff.setPhone(request.phone());
        staff.setPassword(request.password());
        staff.setRole("STAFF");

        if (staff.getPassword() != null && !staff.getPassword().isBlank()) {
            staff.setPassword(passwordEncoder.encode(staff.getPassword()));
        }

        Staff saved = staffRepository.save(staff);

        return new StaffResponse(
                saved.getStaffId(),
                saved.getName(),
                saved.getRole(),
                saved.getEmail(),
                saved.getPhone(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    public StaffResponse update(Long id, StaffRequest request) {

        Staff existing = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found: " + id));

        existing.setName(request.name());
        existing.setRole("STAFF");
        existing.setEmail(request.email());
        existing.setPhone(request.phone());

        if (request.password() != null &&
                !request.password().isBlank()) {
            existing.setPassword(
                    passwordEncoder.encode(request.password())
            );
        }

        Staff saved = staffRepository.save(existing);

        return new StaffResponse(
                saved.getStaffId(),
                saved.getName(),
                saved.getRole(),
                saved.getEmail(),
                saved.getPhone(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    public boolean delete(Long id) {

        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found: " + id));

        staffRepository.delete(staff);

        return true;
    }
}
