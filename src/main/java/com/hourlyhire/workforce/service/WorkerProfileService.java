
package com.hourlyhire.workforce.service;

import com.hourlyhire.workforce.dto.request.*;
import com.hourlyhire.workforce.dto.response.WorkerProfileResponse;
import com.hourlyhire.workforce.dto.response.WorkerPublicProfileResponse;
import com.hourlyhire.workforce.enums.ProfileStatus;
import com.hourlyhire.workforce.enums.VerificationStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkerProfileService {

    // Create worker profile for authenticated user
    WorkerProfileResponse createWorkerProfile(
            CreateWorkerProfileRequest request,
            Long userId
    );

    // Retrieve worker profile by profile ID
    WorkerProfileResponse getWorkerProfile(Long workerId);

    // Check whether user already has a worker profile
    boolean existsByUserId(Long userId);

    // Retrieve worker profile using UserAccount ID
    WorkerProfileResponse getWorkerProfileByUserId(Long userId);
    WorkerProfileResponse updateWorkerProfile(
            UpdateWorkerProfileRequest request,
            Long userId
    );

    WorkerProfileResponse updateVisibility(
            UpdateVisibilityRequest request,
            Long userId
    );

    // ADMIN: Update worker profile status
    WorkerProfileResponse updateProfileStatus(
            Long workerId,
            UpdateProfileStatusRequest request
    );

    // ADMIN: Update worker verification status
    WorkerProfileResponse updateVerificationStatus(
            Long workerId,
            UpdateVerificationStatusRequest request
    );

    // ADMIN: Retrieve all worker profiles
    Page<WorkerProfileResponse> getAllWorkerProfiles(
            Pageable pageable
    );
    WorkerPublicProfileResponse getPublicWorkerProfile(Long workerId);
    Page<WorkerPublicProfileResponse> getDiscoverableWorkers(
            Pageable pageable
    );

    // Retrieve worker profiles using filters
    Page<WorkerProfileResponse> getWorkerProfilesByFilters(
            ProfileStatus profileStatus,
            VerificationStatus verificationStatus,
            Boolean discoverable,
            Pageable pageable
    );
}
