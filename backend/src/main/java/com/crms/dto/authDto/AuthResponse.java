package com.crms.dto.authDto;

public record AuthResponse(
        String token,
        String role
) {}
