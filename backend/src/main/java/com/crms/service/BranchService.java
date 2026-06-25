package com.crms.service;

import com.crms.dto.branch.BranchRequest;
import com.crms.dto.branch.BranchResponse;
import com.crms.dto.car.CarResponse;
import com.crms.dto.staff.StaffResponse;
import com.crms.model.*;
import com.crms.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;
    private final JdbcTemplate jdbcTemplate;

    public List<BranchResponse> listBranches() {
        return branchRepository.findAll().stream().map(branch -> getResponse(branch)).toList();
    }

    public BranchResponse getById(Long id) {
        return getResponse(branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found: " + id)));
    }

    public BranchResponse addBranch(BranchRequest request) {
        Branch branch = new Branch();
        branch.setName(request.name());
        branch.setPhone(request.phone());
        branch.setAddress(Address.builder()
                .city(request.city())
                .street(request.street())
                .zipcode(request.zipcode())
                .build());

        return getResponse(branchRepository.save(branch));
    }

    public BranchResponse updateBranch(Long id, BranchRequest request) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found: " + id));

        branch.setName(request.name());
        branch.setPhone(request.phone());
        branch.setAddress(Address.builder()
                .city(request.city())
                .street(request.street())
                .zipcode(request.zipcode())
                .build());

        return getResponse(branchRepository.save(branch));
    }

    @Transactional
    public boolean deleteBranch(Long id) {
        if (!branchRepository.existsById(id)) {
            throw new RuntimeException("Branch not found: " + id);
        }

        jdbcTemplate.update("UPDATE staff SET branch_id = NULL WHERE branch_id = ?", id);
        jdbcTemplate.update("UPDATE car SET branch_id = NULL WHERE branch_id = ?", id);

        branchRepository.deleteById(id);
        return true;
    }

    private BranchResponse getResponse(Branch branch) {
        Address address = branch.getAddress();
        Long branchId = branch.getBranchId();

        List<StaffResponse> staffs = branch.getStaffs() == null ? Collections.emptyList()
                : branch.getStaffs().stream()
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

        List<CarResponse> cars = branch.getCars() == null ? Collections.emptyList()
                : branch.getCars().stream()
                .map(car -> {
                    String availability = car.getAvailability();
                    boolean available = availability != null &&
                            ("AVAILABLE".equalsIgnoreCase(availability)
                                    || "TRUE".equalsIgnoreCase(availability));

                    return new CarResponse(
                            car.getCarId(),
                            car.getPlateNumber(),
                            car.getBrand(),
                            car.getModel(),
                            car.getYear(),
                            car.getMileage(),
                            available,
                            availability,
                            car.getDailyRate() == null ? null : car.getDailyRate().doubleValue(),
                            car.getCarType(),
                            branchId,
                            branch.getName()
                    );
                })
                .toList();

        return new BranchResponse(
                branchId,
                branch.getName(),
                branch.getPhone(),
                address == null ? null : address.getCity(),
                address == null ? null : address.getStreet(),
                address == null ? null : address.getZipcode(),
                staffs,
                cars
        );
    }
}
