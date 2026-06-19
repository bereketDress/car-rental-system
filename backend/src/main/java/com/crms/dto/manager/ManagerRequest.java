package com.crms.dto.manager;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ManagerRequest(
        @NotBlank String name,
        @NotBlank String phone,
        @Email @NotBlank String email,
        @NotBlank String password
) {}
