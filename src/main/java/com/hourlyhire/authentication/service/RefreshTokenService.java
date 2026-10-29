package com.hourlyhire.authentication.service;

import com.hourlyhire.authentication.entity.RefreshToken;
import com.hourlyhire.authentication.repository.RefreshTokenRepository;
import com.hourlyhire.security.jwt.JwtService;
import com.hourlyhire.shared.enums.ErrorCode;
import com.hourlyhire.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    public record Rotated(Long userId, String newRawToken) {
    }

    /** Creates a new refresh token in the given login "family" and returns the raw value. */
    @Transactional
    public String issue(Long userId, UUID familyId) {
        String raw = generateRawToken();
        Instant expiresAt = Instant.now()
                .plus(jwtService.getRefreshExpirationDays(), ChronoUnit.DAYS);

        refreshTokenRepository.save(
                new RefreshToken(userId, hash(raw), familyId, expiresAt));
        return raw;
    }

    /**
     * Validates the presented token, revokes it, and issues the next one in the same family.
     * noRollbackFor: when reuse is detected we must COMMIT the family revocation
     * even though we throw.
     */
    @Transactional(noRollbackFor = BusinessException.class)
    public Rotated rotate(String rawToken) {
        Instant now = Instant.now();

        RefreshToken current = refreshTokenRepository
                .findByTokenHashForUpdate(hash(rawToken))
                .orElseThrow(this::invalidToken);

        if (current.isRevoked()) {
            // An already-used token was presented again: assume it was stolen.
            refreshTokenRepository.revokeFamily(current.getFamilyId(), now);
            throw invalidToken();
        }
        if (current.isExpired(now)) {
            throw invalidToken();
        }

        current.revoke(now);
        String newRaw = issue(current.getUserId(), current.getFamilyId());
        return new Rotated(current.getUserId(), newRaw);
    }

    /** Logout: revokes the whole login family. Unknown tokens are ignored (idempotent). */
    @Transactional
    public void revoke(String rawToken) {
        refreshTokenRepository.findByTokenHash(hash(rawToken))
                .ifPresent(t -> refreshTokenRepository
                        .revokeFamily(t.getFamilyId(), Instant.now()));
    }

    @Transactional
    public void revokeAllForUser(Long userId) {
        refreshTokenRepository.revokeAllForUser(userId, Instant.now());
    }

    private BusinessException invalidToken() {
        return new BusinessException(ErrorCode.INVALID_TOKEN,
                "Refresh token is invalid or expired.");
    }

    private static String generateRawToken() {
        byte[] bytes = new byte[48];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String hash(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(
                    digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}