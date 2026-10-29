package com.hourlyhire.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String issuer,
        long expirationSeconds,
        String secretBase64,
        @DefaultValue("7") long refreshExpirationDays
) {
}