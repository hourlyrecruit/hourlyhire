package com.hourlyhire.workforce.controller;

import com.hourlyhire.workforce.dto.request.*;
import com.hourlyhire.workforce.dto.response.WorkerProfileResponse;
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

    @Operation(
            summary = "Create Worker Profile",
            description = """
            Creates a worker profile for an existing user.

            Authentication: Required.
            Intended access: WORKER.
            Access restriction: A worker can create their own profile.
            Business rule: One worker profile per user.
            """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<WorkerProfileResponse> createWorkerProfile(
            @Valid @RequestBody CreateWorkerProfileRequest request) {
        WorkerProfileResponse response
                = workerProfileService.createWorkerProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get Worker Profile",
            description = """
            Retrieves a worker profile using the worker profile ID.

            Authentication: Not Required.
            Intended access: PUBLIC.
            Access restriction: Only fields approved for public visibility
            should be exposed.
            """,
            security = {}
    )
    @GetMapping("/{workerId}")
    @PreAuthorize("hasAnyRole('WORKER', 'EMPLOYER', 'ADMIN')")
    public ResponseEntity<WorkerProfileResponse> getWorkerProfile(@PathVariable Long workerId)
    {
        WorkerProfileResponse response = workerProfileService.getWorkerProfile(workerId);
         return ResponseEntity.ok(response);
    }


    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<WorkerProfileResponse> getWorkerProfileByUserId(
            @PathVariable Long userId) {

        WorkerProfileResponse response =
                workerProfileService.getWorkerProfileByUserId(userId);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update Worker Profile",
            description = """
            Updates the bio and profile photo of an existing worker.

            Authentication: Required.
            Intended access: WORKER.
            Access restriction: Workers can update only their own profiles.
            """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{workerId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR_ADMIN')")
    public ResponseEntity<WorkerProfileResponse> updateWorkerProfile(
            @PathVariable Long workerId,
            @Valid @RequestBody UpdateWorkerProfileRequest request) {

        WorkerProfileResponse response =
                workerProfileService.updateWorkerProfile(workerId, request);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update Worker Profile Visibility",
            description = """
                Allows a worker to enable or disable profile discovery.
                Authentication: Required.
                Intended access: WORKER.
                """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/{workerId}/visibility")
    public ResponseEntity<WorkerProfileResponse> updateVisibility(
            @PathVariable Long workerId,
            @Valid @RequestBody UpdateVisibilityRequest request) {

        WorkerProfileResponse response =
                workerProfileService.updateVisibility(workerId, request);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update Worker Profile Status",
            description = """
            Changes a worker profile status between ACTIVE and INACTIVE.

            Authentication: Required.
            Intended access: WORKER, ADMIN.
            Access restriction: Workers can update their own profile status.
            ADMIN can manage worker profile status.
            """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/{workerId}/status")
    public ResponseEntity<WorkerProfileResponse> updateProfileStatus(
            @PathVariable Long workerId,
            @Valid @RequestBody UpdateProfileStatusRequest request) {

        WorkerProfileResponse response =
                workerProfileService.updateProfileStatus(workerId, request);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update Worker Verification Status",
            description = """
            Updates the verification status of a worker profile.

            Authentication: Required.
            Intended access: ADMIN, HR.
            Access restriction: Only authorized verification personnel
            can approve or reject worker verification.
            """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/{workerId}/verification-status")
    public ResponseEntity<WorkerProfileResponse> updateVerificationStatus(
            @PathVariable Long workerId,
            @Valid @RequestBody UpdateVerificationStatusRequest request) {

        WorkerProfileResponse response =
                workerProfileService.updateVerificationStatus(workerId, request);

        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Get All Worker Profiles",
            description = """
            Retrieves worker profiles using pagination and sorting.

            Authentication: Required.
            Intended access: ADMIN, HR.
            Access restriction: Only authorized administrative users
            can retrieve the complete worker profile list.
            """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<Page<WorkerProfileResponse>> getAllWorkerProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WorkerProfileResponse> response =
                workerProfileService.getAllWorkerProfiles(pageable);

        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Filter Worker Profiles",
            description = """
            Retrieves worker profiles based on profile status,
            verification status and discoverability.

            Authentication: Required.
            Intended access: ADMIN, HR, EMPLOYER.
            Access restriction: Employers can only discover profiles
            that meet the marketplace visibility and verification rules.
            """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/filter")
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

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WorkerProfileResponse> response =
                workerProfileService.getWorkerProfilesByFilters(
                        profileStatus,
                        verificationStatus,
                        discoverable,
                        pageable
                );

        return ResponseEntity.ok(response);
    }


}