package com.hourlyhire.authentication.dto;

import com.hourlyhire.authentication.enums.Role;

public record MeResponse(Long userId, String email, Role role, String accountStatus) {
}