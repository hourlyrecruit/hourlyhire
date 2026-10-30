package com.hourlyhire.authentication.service;

import com.hourlyhire.authentication.dto.*;
import com.hourlyhire.authentication.entity.UserAccount;
import com.hourlyhire.authentication.enums.Role;
import com.hourlyhire.authentication.repository.UserAccountRepository;
import com.hourlyhire.security.jwt.JwtService;
import com.hourlyhire.security.userDetails.CustomUserDetails;
import com.hourlyhire.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

import static com.hourlyhire.shared.enums.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String TOKEN_TYPE = "Bearer";
    private static final int MAX_PAGE_SIZE = 100;

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    // ------------------------------------------------------------------
    // Register
    // ------------------------------------------------------------------
    @Transactional
    public Long register(RegisterRequest request) {
        // Public sign-up may only create WORKER or EMPLOYER accounts.
        if (request.getRole() == Role.ADMIN) {
            throw new BusinessException(ACCESS_DENIED,
                    "This role cannot be selected during registration.");
        }
        return createAccount(request);
    }

    /** Admin-only path (guarded in the controller): any role is allowed. */
    @Transactional
    public Long createAccountAsAdmin(RegisterRequest request) {
        return createAccount(request);
    }

    private Long createAccount(RegisterRequest request) {
        String email = normalizeEmail(request.getEmail());
        String username = request.getUsername().trim();

        if (userAccountRepository.existsByEmail(email)) {
            throw new BusinessException(EMAIL_ALREADY_EXISTS,
                    "An account with this email already exists.");
        }

        String hash = passwordEncoder.encode(request.getPassword());

        try {
            // saveAndFlush so a unique-constraint race is thrown here,
            // not later at commit time where it can't be caught.
            return userAccountRepository
                    .saveAndFlush(new UserAccount(email, username, hash, request.getRole()))
                    .getId();
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(EMAIL_ALREADY_EXISTS,
                    "An account with this email already exists.");
        }
    }

    // ------------------------------------------------------------------
    // Login
    // ------------------------------------------------------------------
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.getEmail());

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email, request.getPassword())
            );
        } catch (AuthenticationException ex) {
            // Same message for unknown email, wrong password, locked or
            // disabled accounts, so nothing about the account is leaked.
            throw new BusinessException(INVALID_CREDENTIALS,
                    "Invalid email or password.");
        }

        UserAccount user =
                ((CustomUserDetails) authentication.getPrincipal()).getUser();

        // A new login starts a new refresh-token "family".
        String refreshToken =
                refreshTokenService.issue(user.getId(), UUID.randomUUID());

        return buildAuthResponse(user, refreshToken);
    }

    // ------------------------------------------------------------------
    // Refresh (rotation)
    // ------------------------------------------------------------------
    // noRollbackFor: if token reuse is detected, RefreshTokenService revokes
    // the whole family and throws. That revocation must be committed, so this
    // outer transaction must not roll back on BusinessException.
    @Transactional(noRollbackFor = BusinessException.class)
    public AuthResponse refresh(String rawRefreshToken) {
        RefreshTokenService.Rotated rotated =
                refreshTokenService.rotate(rawRefreshToken);

        UserAccount user = userAccountRepository.findById(rotated.userId())
                .orElseThrow(() -> new BusinessException(INVALID_TOKEN,
                        "Refresh token is invalid or expired."));

        if (user.getAccountStatus() != UserAccount.AccountStatus.ACTIVE) {
            refreshTokenService.revokeAllForUser(user.getId());
            throw new BusinessException(INVALID_TOKEN,
                    "Refresh token is invalid or expired.");
        }

        return buildAuthResponse(user, rotated.newRawToken());
    }

    // ------------------------------------------------------------------
    // Logout
    // ------------------------------------------------------------------
    public void logout(String rawRefreshToken) {
        refreshTokenService.revoke(rawRefreshToken);
    }

    // ------------------------------------------------------------------
    // Current user
    // ------------------------------------------------------------------
    @Transactional(readOnly = true)
    public MeResponse me(Long userId) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(INVALID_TOKEN,
                        "User no longer exists."));

        return new MeResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                user.getAccountStatus().name()
        );
    }

    // ------------------------------------------------------------------
    // Admin: list / get users
    // ------------------------------------------------------------------
    @Transactional(readOnly = true)
    public PageResponse<UserSummary> listUsers(int page, int size) {
        // Fixed order by id so paging is stable; client cannot choose a sort.
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), MAX_PAGE_SIZE),
                Sort.by("id"));

        return PageResponse.from(
                userAccountRepository.findAll(pageable)
                        .map(this::toSummary));
    }

    @Transactional(readOnly = true)
    public UserSummary getUser(Long id) {
        return userAccountRepository.findById(id)
                .map(this::toSummary)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND,
                        "User not found."));
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------
    private UserSummary toSummary(UserAccount u) {
        return new UserSummary(
                u.getId(),
                u.getEmail(),
                u.getUsername(),
                u.getRole(),
                u.getAccountStatus().name(),
                u.getCreatedAt()
        );

    }

    private AuthResponse buildAuthResponse(UserAccount user, String refreshToken) {
        return new AuthResponse(
                jwtService.generateAccessToken(user),
                refreshToken,
                TOKEN_TYPE,
                jwtService.getExpirationSeconds(),
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole()
        );
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}