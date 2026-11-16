
package com.hourlyhire.workforce.controller;

import com.hourlyhire.security.utils.SecurityUtils;
import com.hourlyhire.workforce.dto.request.*;
import com.hourlyhire.workforce.dto.response.WorkerProfileResponse;
import com.hourlyhire.workforce.dto.response.WorkerPublicProfileResponse;
import com.hourlyhire.workforce.enums.ProfileStatus;
import com.hourlyhire.workforce.enums.VerificationStatus;
import com.hourlyhire.workforce.service.WorkerProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workforce/profiles")
@RequiredArgsConstructor
public class WorkerProfileController {

    private final WorkerProfileService workerProfileService;

    // 1. Create Worker Profile
    @Operation(
            summary = "Create Worker Profile",
            description = """
            Creates a worker profile for the authenticated user.
            Authentication: Required.
            Intended access: WORKER.
            One worker profile per user.
            """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<WorkerProfileResponse> createWorkerProfile(
            @Valid @RequestBody CreateWorkerProfileRequest request) {

        Long userId = SecurityUtils.getCurrentUserId();

        WorkerProfileResponse response =
                workerProfileService.createWorkerProfile(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get My Worker Profile
    @Operation(
            summary = "Get My Worker Profile",
            description = "Retrieves the authenticated worker's own profile.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/me")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<WorkerProfileResponse> getMyProfile() {

        Long userId = SecurityUtils.getCurrentUserId();

        WorkerProfileResponse response =
                workerProfileService.getWorkerProfileByUserId(userId);

        return ResponseEntity.ok(response);
    }

    // 3. Get Worker Profile by Profile ID
    @GetMapping("/{workerId}")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    @Operation(
            summary = "Get Public Worker Profile",
            description = """
        Retrieves an eligible worker profile.
        Only ACTIVE, VERIFIED and discoverable profiles are returned.
        Authentication: Required.
        Access: EMPLOYER, ADMIN.
        """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<WorkerPublicProfileResponse> getWorkerProfile(
            @PathVariable Long workerId) {

        WorkerPublicProfileResponse response =
                workerProfileService.getPublicWorkerProfile(workerId);

        return ResponseEntity.ok(response);
    }

    // 4. Get Worker Profile by User ID - ADMIN
    @Operation(
            summary = "Get Worker Profile by User ID",
            description = "Allows ADMIN to retrieve a worker profile by account ID.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkerProfileResponse> getWorkerProfileByUserId(
            @PathVariable Long userId) {

        WorkerProfileResponse response =
                workerProfileService.getWorkerProfileByUserId(userId);

        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Get Discoverable Workers ",
            description = """
        Retrieves active, verified and discoverable worker profiles.
        Authentication: Required.
        Access: EMPLOYER.
        Supports pagination and sorting.
        """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/discoverable")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Page<WorkerPublicProfileResponse>>
    getDiscoverableWorkers(

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Pageable pageable = createPageable(
                page, size, sortBy, sortDirection);

        Page<WorkerPublicProfileResponse> response =
                workerProfileService.getDiscoverableWorkers(pageable);

        return ResponseEntity.ok(response);
    }

    // 5. Update My Worker Profile
    @Operation(
            summary = "Update My Worker Profile",
            description = "Allows a worker to update their own bio and profile photo.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/me")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<WorkerProfileResponse> updateWorkerProfile(
            @Valid @RequestBody UpdateWorkerProfileRequest request) {

        Long userId = SecurityUtils.getCurrentUserId();

        WorkerProfileResponse response =
                workerProfileService.updateWorkerProfile(request, userId);

        return ResponseEntity.ok(response);
    }

    // 6. Update My Profile Visibility
    @Operation(
            summary = "Update My Profile Visibility",
            description = "Allows a worker to enable or disable profile discovery.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/me/visibility")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<WorkerProfileResponse> updateVisibility(
            @Valid @RequestBody UpdateVisibilityRequest request) {

        Long userId = SecurityUtils.getCurrentUserId();

        WorkerProfileResponse response =
                workerProfileService.updateVisibility(request, userId);

        return ResponseEntity.ok(response);
    }

    // 7. ADMIN: Update Worker Profile Status
    @Operation(
            summary = "Update Worker Profile Status",
            description = "Allows ADMIN to activate, deactivate or suspend a worker profile.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/{workerId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkerProfileResponse> updateProfileStatus(
            @PathVariable Long workerId,
            @Valid @RequestBody UpdateProfileStatusRequest request) {

        WorkerProfileResponse response =
                workerProfileService.updateProfileStatus(workerId, request);

        return ResponseEntity.ok(response);
    }

    // 8. ADMIN: Update Worker Verification Status
    @Operation(
            summary = "Update Worker Verification Status",
            description = "Allows ADMIN to approve or reject worker verification.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/{workerId}/verification-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkerProfileResponse> updateVerificationStatus(
            @PathVariable Long workerId,
            @Valid @RequestBody UpdateVerificationStatusRequest request) {

        WorkerProfileResponse response =
                workerProfileService.updateVerificationStatus(
                        workerId, request);

        return ResponseEntity.ok(response);
    }

    // 9. ADMIN: Get All Worker Profiles
    @Operation(
            summary = "Get All Worker Profiles",
            description = "Allows ADMIN to retrieve worker profiles with pagination.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<WorkerProfileResponse>> getAllWorkerProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Pageable pageable = createPageable(
                page, size, sortBy, sortDirection);

        Page<WorkerProfileResponse> response =
                workerProfileService.getAllWorkerProfiles(pageable);

        return ResponseEntity.ok(response);
    }

    // 10. Filter Worker Profiles
    @Operation(
            summary = "Filter Worker Profiles",
            description = """
            ADMIN can filter worker profiles using all available filters.
            EMPLOYER can discover only ACTIVE, VERIFIED and discoverable workers.
            """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<Page<WorkerProfileResponse>> getWorkerProfilesByFilters(

            @RequestParam(required = false)
            ProfileStatus profileStatus,

            @RequestParam(required = false)
            VerificationStatus verificationStatus,

            @RequestParam(required = false)
            Boolean discoverable,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "desc")
            String sortDirection) {

        Pageable pageable = createPageable(
                page, size, sortBy, sortDirection);

        // Restrict employer searches to eligible workers
        if (SecurityUtils.getCurrentRole().name().equals("EMPLOYER")) {
            profileStatus = ProfileStatus.ACTIVE;
            verificationStatus = VerificationStatus.VERIFIED;
            discoverable = true;
        }

        Page<WorkerProfileResponse> response =
                workerProfileService.getWorkerProfilesByFilters(
                        profileStatus,
                        verificationStatus,
                        discoverable,
                        pageable
                );

        return ResponseEntity.ok(response);
    }

    // Common pagination and sorting method
    private Pageable createPageable(
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Invalid pagination values. Page must be >= 0 and size must be between 1 and 100.");
        }

        if (!sortBy.equals("createdAt")
                && !sortBy.equals("updatedAt")
                && !sortBy.equals("id")) {
            throw new IllegalArgumentException(
                    "Invalid sort field.");
        }

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        return PageRequest.of(page, size, sort);
    }
}