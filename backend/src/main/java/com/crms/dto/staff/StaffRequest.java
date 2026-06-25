package com.crms.dto.staff;

import jakarta.validation.constraints.NotBlank;

public record StaffRequest(

        @NotBlank String name,
        @NotBlank String role,
        @NotBlank String email,
        @NotBlank String phone

) {}
