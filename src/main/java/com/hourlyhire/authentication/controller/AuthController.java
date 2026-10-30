package com.hourlyhire.authentication.controller;

import com.hourlyhire.authentication.dto.AuthResponse;
import com.hourlyhire.authentication.dto.LoginRequest;
import com.hourlyhire.authentication.dto.MeResponse;
import com.hourlyhire.authentication.dto.RefreshTokenRequest;
import com.hourlyhire.authentication.dto.RegisterRequest;
import com.hourlyhire.authentication.service.AuthService;
import com.hourlyhire.security.jwt.AuthenticatedUser;
import com.hourlyhire.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.hourlyhire.authentication.dto.ForgotPasswordRequest;
import com.hourlyhire.authentication.dto.ResetPasswordRequest;
import com.hourlyhire.authentication.service.PasswordResetService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "01-AuthController", description = "User Authentication APIs")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    @Operation(summary = "User Registration. Access: All")
    public ResponseEntity<ApiResponse<Map<String, Long>>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {

        Long userId = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Account registered successfully.",
                        Map.of("userId", userId),
                        httpRequest.getRequestURI()));
    }

    @PostMapping("/login")
    @Operation(summary = "User Login. Access: All")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        return ResponseEntity.ok(ApiResponse.success(
                "Login successful.",
                authService.login(request),
                httpRequest.getRequestURI()));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rotate refresh token and get a new access token. Access: All")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {

        return ResponseEntity.ok(ApiResponse.success(
                "Token refreshed successfully.",
                authService.refresh(request.getRefreshToken()),
                httpRequest.getRequestURI()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout (revokes the refresh token family). Access: All")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {

        authService.logout(request.getRefreshToken());

        return ResponseEntity.ok(ApiResponse.success(
                "Logged out successfully.",
                null,
                httpRequest.getRequestURI()));
    }

    @GetMapping("/me")
    @Operation(summary = "Current user profile. Access: Authenticated",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<MeResponse>> me(
            @AuthenticationPrincipal AuthenticatedUser user,
            HttpServletRequest httpRequest) {

        return ResponseEntity.ok(ApiResponse.success(
                "Current user retrieved successfully.",
                authService.me(user.id()),
                httpRequest.getRequestURI()));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request a password reset link. Access: All")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest) {

        passwordResetService.requestReset(request.getEmail());

        return ResponseEntity.ok(ApiResponse.success(
                "If an account exists for this email, a reset link has been sent.",
                null,
                httpRequest.getRequestURI()));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Set a new password using the emailed token. Access: All")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest) {

        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok(ApiResponse.success(
                "Password reset successfully. Please log in with your new password.",
                null,
                httpRequest.getRequestURI()));
    }
}