
package com.hourlyhire.workforce.controller;

import com.hourlyhire.authentication.enums.Role;
import com.hourlyhire.security.jwt.AuthenticatedUser;

import com.hourlyhire.shared.enums.ErrorCode;
import com.hourlyhire.shared.exception.BusinessException;

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

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workforce/employers")
@RequiredArgsConstructor
@Tag(name = "04 Employer Profile", description = "Employer profile management APIs")
public class EmployerProfileController {

    private final EmployerProfileService employerProfileService;

    // 1. Create employer profile
    @PostMapping
    @Operation(summary = "Create employer profile",
            description = """
            EMPLOYER can create a profile for their own account.
            ADMIN can create a profile for another EMPLOYER account.
            """
    )
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<EmployerProfileResponse> createProfile(
            @Valid @RequestBody CreateEmployerProfileRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        Long targetUserId;

        if (currentUser.role() == Role.ADMIN) {

            targetUserId = request.getUserId();

            if (targetUserId == null) {
                throw new BusinessException(
                        ErrorCode.VALIDATION_FAILED,
                        "User ID is required for admin profile creation."
                );
            }

        } else {
            targetUserId = currentUser.id();
        }

        EmployerProfileResponse response =
                employerProfileService.createProfile(
                        request,
                        targetUserId
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 2. Get employer profile by profile ID
    @GetMapping("/{employerId}")
    @Operation(summary = "Get employer profile by ID")
    @PreAuthorize("hasAnyRole('WORKER', 'EMPLOYER', 'ADMIN')")
    public ResponseEntity<EmployerProfileResponse> getProfileById(
            @PathVariable Long employerId,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        EmployerProfileResponse response =
                employerProfileService.getProfileById(
                        employerId,
                        currentUser.id(),
                        currentUser.role()
                );

        return ResponseEntity.ok(response);
    }

    // 3. Get employer profile by UserAccount ID
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get employer profile by user ID")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<EmployerProfileResponse> getProfileByUserId(
            @PathVariable Long userId,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        EmployerProfileResponse response =
                employerProfileService.getProfileByUserId(
                        userId,
                        currentUser.id(),
                        currentUser.role()
                );

        return ResponseEntity.ok(response);
    }

    // 4. Update employer profile
    @PutMapping("/{employerId}")
    @Operation(
            summary = "Update employer profile",
            description = "Updates supplied non-null employer profile fields."
    )
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<EmployerProfileResponse> updateProfile(
            @PathVariable Long employerId,
            @Valid @RequestBody UpdateEmployerProfileRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        EmployerProfileResponse response =
                employerProfileService.updateProfile(
                        employerId,
                        request,
                        currentUser.id(),
                        currentUser.role()
                );

        return ResponseEntity.ok(response);
    }

    // 5. Activate or deactivate employer profile
    @PatchMapping("/{employerId}/status")
    @Operation(summary = "Update employer profile status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployerProfileResponse> updateProfileStatus(
            @PathVariable Long employerId,
            @RequestParam ProfileStatus status) {

        EmployerProfileResponse response =
                employerProfileService.updateProfileStatus(
                        employerId,
                        status
                );

        return ResponseEntity.ok(response);
    }

    // 6. Get all employer profiles
    @GetMapping
    @Operation(summary = "Get all employer profiles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<EmployerProfileResponse>> getAllProfiles(
            @ParameterObject
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id"
            ) Pageable pageable) {

        return ResponseEntity.ok(
                employerProfileService.getAllProfiles(pageable)
        );
    }

    // 7. Filter employer profiles
    @GetMapping("/filter")
    @Operation(summary = "Filter employer profiles")
    @PreAuthorize("hasAnyRole('WORKER', 'ADMIN')")
    public ResponseEntity<Page<EmployerProfileResponse>> getFilteredProfiles(
            @RequestParam(required = false) EmployerType employerType,
            @RequestParam(required = false) ProfileStatus profileStatus,
            @ParameterObject
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id"
            ) Pageable pageable,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        return ResponseEntity.ok(
                employerProfileService.getFilteredProfiles(
                        employerType,
                        profileStatus,
                        pageable,
                        currentUser.role()
                )
        );
    }

    // 8. Get logged-in employer's own profile
    @GetMapping("/my-profile")
    @Operation(summary = "Get my employer profile")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<EmployerProfileResponse> getMyProfile(
            @AuthenticationPrincipal AuthenticatedUser currentUser) {

        return ResponseEntity.ok(
                employerProfileService.getProfileByUserId(
                        currentUser.id(),
                        currentUser.id(),
                        currentUser.role()
                )
        );
    }
}
