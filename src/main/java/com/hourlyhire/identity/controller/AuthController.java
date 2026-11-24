
package com.hourlyhire.identity.controller;

import com.hourlyhire.identity.dto.RegisterRequest;
import com.hourlyhire.identity.service.AuthService;
import com.hourlyhire.shared.response.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Long>>> register(
            @Valid @RequestBody RegisterRequest request) {

        Long userId = authService.register(request);

        Map<String, Long> data = Map.of("userId", userId);

        ApiResponse<Map<String, Long>> response =
                ApiResponse.success(
                        "User registered successfully",
                        data,
                        "/api/v1/auth/register"
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
