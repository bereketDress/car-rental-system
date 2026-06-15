package com.crms.dto.branchDTO;

import jakarta.validation.constraints.NotBlank;

public record BranchRequest(

        @NotBlank String name,
        @NotBlank String phone,
        @NotBlank String city,
        @NotBlank String street,
        @NotBlank String zipcode

) {}