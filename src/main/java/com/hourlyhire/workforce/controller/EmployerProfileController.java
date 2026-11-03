package com.hourlyhire.workforce.controller;

import com.hourlyhire.workforce.dto.request.CreateEmployerProfileRequest;
import com.hourlyhire.workforce.dto.request.UpdateEmployerProfileRequest;
import com.hourlyhire.workforce.dto.response.EmployerProfileResponse;
import com.hourlyhire.workforce.enums.EmployerType;
import com.hourlyhire.workforce.enums.ProfileStatus;
import com.hourlyhire.workforce.service.EmployerProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workforce/employers")
@RequiredArgsConstructor
@Tag(name = "Employer Profile", description = "Employer profile management APIs")
public class EmployerProfileController {

    private final EmployerProfileService employerProfileService;

    // 1. Create employer profile
    @PostMapping
    @Operation(
            summary = "Create employer profile",
            description = "Creates a new employer profile for an existing user."
    )
    public ResponseEntity<EmployerProfileResponse> createProfile(
            @Valid @RequestBody CreateEmployerProfileRequest request) {

        EmployerProfileResponse response =
                employerProfileService.createProfile(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get employer profile by profile ID
    @GetMapping("/{employerId}")
    @Operation(summary = "Get employer profile by ID")
    public ResponseEntity<EmployerProfileResponse> getProfileById(
            @PathVariable Long employerId) {

        EmployerProfileResponse response =
                employerProfileService.getProfileById(employerId);

        return ResponseEntity.ok(response);
    }

    // 3. Get employer profile by user ID
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get employer profile by user ID")
    public ResponseEntity<EmployerProfileResponse> getProfileByUserId(
            @PathVariable Long userId) {

        EmployerProfileResponse response =
                employerProfileService.getProfileByUserId(userId);

        return ResponseEntity.ok(response);
    }

    // 4. Update employer profile
    @PutMapping("/{employerId}")
    @Operation(
            summary = "Update employer profile",
            description = "Updates the supplied non-null employer profile fields."
    )
    public ResponseEntity<EmployerProfileResponse> updateProfile(
            @PathVariable Long employerId,
            @Valid @RequestBody UpdateEmployerProfileRequest request) {

        EmployerProfileResponse response =
                employerProfileService.updateProfile(employerId, request);

        return ResponseEntity.ok(response);
    }

    // 5. Activate or deactivate employer profile
    @PatchMapping("/{employerId}/status")
    @Operation(summary = "Update employer profile status")
    public ResponseEntity<EmployerProfileResponse> updateProfileStatus(
            @PathVariable Long employerId,
            @RequestParam ProfileStatus status) {

        EmployerProfileResponse response =
                employerProfileService.updateProfileStatus(
                        employerId, status
                );

        return ResponseEntity.ok(response);
    }

    // 6. Get all employer profiles with pagination
    @GetMapping
    @Operation(summary = "Get all employer profiles")
    public ResponseEntity<Page<EmployerProfileResponse>> getAllProfiles(
            @ParameterObject
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id"
            ) Pageable pageable) {

        Page<EmployerProfileResponse> response =
                employerProfileService.getAllProfiles(pageable);

        return ResponseEntity.ok(response);
    }

    // 7. Filter employer profiles
    @GetMapping("/filter")
    @Operation(summary = "Filter employer profiles")
    public ResponseEntity<Page<EmployerProfileResponse>> getFilteredProfiles(
            @RequestParam(required = false) EmployerType employerType,
            @RequestParam(required = false) ProfileStatus profileStatus,
            @ParameterObject
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id"
            ) Pageable pageable) {

        Page<EmployerProfileResponse> response =
                employerProfileService.getFilteredProfiles(
                        employerType,
                        profileStatus,
                        pageable
                );

        return ResponseEntity.ok(response);
    }
}
