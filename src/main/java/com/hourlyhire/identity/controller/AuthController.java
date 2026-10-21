package com.hourlyhire.identity.controller;

import com.hourlyhire.identity.dto.RegisterRequest;
import com.hourlyhire.identity.service.AuthService;
import com.hourlyhire.shared.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Long>>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {

        Long userId = authService.register(request);

        ApiResponse<Map<String, Long>> response =
                ApiResponse.success(
                        "Account registered successfully.",
                        Map.of("userId", userId),
                        httpRequest.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
