package com.hourlyhire.authentication.controller;

import com.hourlyhire.authentication.dto.PageResponse;
import com.hourlyhire.authentication.dto.RegisterRequest;
import com.hourlyhire.authentication.dto.UserSummary;
import com.hourlyhire.authentication.service.AuthService;
import com.hourlyhire.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/users")
@Tag(name = "02-AdminController", description = "Admin APIs")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final AuthService authService;

    @PostMapping
    @Operation(summary = "Create a user with any role. Access: ADMIN",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Map<String, Long>>> create(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {

        Long id = authService.createAccountAsAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Account created.", Map.of("userId", id),
                        httpRequest.getRequestURI()));
    }
    @GetMapping
    @Operation(summary = "List users (paged). Access: ADMIN",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PageResponse<UserSummary>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {

        return ResponseEntity.ok(ApiResponse.success(
                "Users retrieved successfully.",
                authService.listUsers(page, size),
                httpRequest.getRequestURI()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one user. Access: ADMIN",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserSummary>> get(
            @PathVariable Long id, HttpServletRequest httpRequest) {

        return ResponseEntity.ok(ApiResponse.success(
                "User retrieved successfully.",
                authService.getUser(id),
                httpRequest.getRequestURI()));
    }
}