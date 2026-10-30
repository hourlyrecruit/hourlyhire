package com.hourlyhire.authentication.event;

import java.time.Instant;

public record PasswordResetRequestedEvent(
        Long userId,
        String email,
        String username,
        String rawToken,
        Instant expiresAt
) {}