package com.crms.dto.auth;

public record AuthResponse(
        String token,
        String role
) {}
