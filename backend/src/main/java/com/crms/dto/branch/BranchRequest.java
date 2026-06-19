package com.crms.dto.branch;

import jakarta.validation.constraints.NotBlank;

public record BranchRequest(

        @NotBlank String name,
        String phone,
        String city,
        String street,
        String zipcode

) {}
