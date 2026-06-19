package com.crms.dto.manager;

public record ManagerResponse(
        Long managerId,
        String name,
        String email,
        String phone
) {}
