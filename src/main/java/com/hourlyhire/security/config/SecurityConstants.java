package com.hourlyhire.security.config;

public class SecurityConstants {
    // "/api/v1/auth/me" is deliberately NOT public, so it requires a valid token.
    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/api/v1/auth/logout",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/actuator/health"
    };

    private SecurityConstants() {}
}