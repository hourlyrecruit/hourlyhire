package com.hourlyhire.authentication.service;

import com.hourlyhire.authentication.entity.PasswordResetToken;
import com.hourlyhire.authentication.entity.UserAccount;
import com.hourlyhire.authentication.entity.UserAccount.AccountStatus;
import com.hourlyhire.authentication.event.PasswordResetRequestedEvent;
import com.hourlyhire.authentication.repository.PasswordResetTokenRepository;
import com.hourlyhire.authentication.repository.UserAccountRepository;
import com.hourlyhire.shared.enums.ErrorCode;
import com.hourlyhire.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Duration COOLDOWN = Duration.ofSeconds(60);

    private final UserAccountRepository userAccountRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher events;

    @Value("${app.password-reset.expiration-minutes:15}")
    private long expirationMinutes;

    /** Never reveals whether the email exists: always returns normally. */
    @Transactional
    public void requestReset(String email) {
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        userAccountRepository.findByEmail(normalized).ifPresent(this::createAndPublish);
    }

    private void createAndPublish(UserAccount user) {
        if (!canReset(user)) {
            return;
        }
        Instant now = Instant.now();
        if (tokenRepository.existsByUserIdAndCreatedAtAfter(
                user.getId(), now.minus(COOLDOWN))) {
            return;
        }

        // Only the newest link works.
        tokenRepository.invalidateAllForUser(user.getId(), now);

        String raw = generateRawToken();
        Instant expiresAt = now.plus(expirationMinutes, ChronoUnit.MINUTES);
        tokenRepository.save(new PasswordResetToken(
                user.getId(), RefreshTokenService.hash(raw), expiresAt));

        events.publishEvent(new PasswordResetRequestedEvent(
                user.getId(), user.getEmail(), user.getUsername(), raw, expiresAt));
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        Instant now = Instant.now();

        PasswordResetToken token = tokenRepository
                .findByTokenHashForUpdate(RefreshTokenService.hash(rawToken.trim()))
                .orElseThrow(this::invalidToken);

        if (token.isUsed() || token.isExpired(now)) {
            throw invalidToken();
        }

        UserAccount user = userAccountRepository.findById(token.getUserId())
                .orElseThrow(this::invalidToken);

        if (!canReset(user)) {
            throw invalidToken();
        }

        user.changePasswordHash(passwordEncoder.encode(newPassword));
        if (user.getAccountStatus() == AccountStatus.PASSWORD_EXPIRED) {
            user.changeAccountStatus(AccountStatus.ACTIVE);
        }

        token.markUsed(now);
        tokenRepository.invalidateAllForUser(user.getId(), now);

        // Log out every device: a reset usually means the old password leaked.
        refreshTokenService.revokeAllForUser(user.getId());
    }

    private boolean canReset(UserAccount user) {
        return user.getAccountStatus() == AccountStatus.ACTIVE
                || user.getAccountStatus() == AccountStatus.PASSWORD_EXPIRED;
    }

    private BusinessException invalidToken() {
        return new BusinessException(ErrorCode.INVALID_RESET_TOKEN,
                "Reset link is invalid or has expired.");
    }

    private static String generateRawToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}