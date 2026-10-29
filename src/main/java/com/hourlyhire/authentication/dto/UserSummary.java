package com.hourlyhire.authentication.dto;

import com.hourlyhire.authentication.enums.Role;
import java.time.Instant;

public record UserSummary(
        Long id,
        String email,
        Role role,
        String accountStatus,
        Instant createdAt
) {}