package com.crms.service;
import com.crms.model.*;
import com.crms.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import com.crms.repository.StaffRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

import static com.crms.util.EntityFields.*;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Staff> getAll() {
        return staffRepository.findAll().stream()
                .filter(staff -> "STAFF".equalsIgnoreCase(string(staff, "role")))
                .toList();
    }

    public Staff getById(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found: " + id));
    }

    public Staff create(Staff staff) {
        set(staff, "role", "STAFF");
        encodePasswordIfPresent(staff);
        attachBranch(staff);
        return staffRepository.save(staff);
    }

    public Staff update(Long id, Staff updated) {
        Staff existing = getById(id);
        set(existing, "name", string(updated, "name"));
        set(existing, "role", "STAFF");
        set(existing, "email", string(updated, "email"));
        set(existing, "phone", string(updated, "phone"));
        set(existing, "branch", get(updated, "branch"));
        String password = string(updated, "password");
        if (password != null && !password.isBlank()) {
            set(existing, "password", passwordEncoder.encode(password));
        }
        attachBranch(existing);
        return staffRepository.save(existing);
    }

    public void delete(Long id) {
        staffRepository.deleteById(id); }

    private void attachBranch(Staff staff) {
        Branch branch = get(staff, "branch", Branch.class);
        if (branch == null || branch.getBranchId() == null) {
            set(staff, "branch", null);
            return;
        }

        Long branchId = branch.getBranchId();
        set(staff, "branch", branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found: " + branchId)));
    }

    private void encodePasswordIfPresent(Staff staff) {
        String password = string(staff, "password");
        if (password != null && !password.isBlank()) {
            set(staff, "password", passwordEncoder.encode(password));
        }
    }
}
