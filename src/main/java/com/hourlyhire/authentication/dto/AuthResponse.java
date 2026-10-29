package com.hourlyhire.authentication.dto;

import com.hourlyhire.authentication.enums.Role;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        Long userId,
        String email,
        Role role
) {
}