package com.crms.dto.branch;


import com.crms.dto.car.CarResponse;
import com.crms.dto.staff.StaffResponse;

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
