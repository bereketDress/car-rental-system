package com.crms.dto.branchDTO;


import com.crms.dto.CarDTO.CarResponse;
import com.crms.dto.staffdto.StaffResponse;

import java.util.List;

public record BranchResponse(
        Long branchId,
        String name,
        String phone,
        String city,
        String street,
        String zipcode,
        List<StaffResponse> staffs,
        List<CarResponse> cars
) {}