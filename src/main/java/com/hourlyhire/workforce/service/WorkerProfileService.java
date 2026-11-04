package com.hourlyhire.workforce.service;

import com.hourlyhire.workforce.dto.request.*;
import com.hourlyhire.workforce.dto.response.WorkerProfileResponse;
import com.hourlyhire.workforce.enums.ProfileStatus;
import com.hourlyhire.workforce.enums.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkerProfileService {

    WorkerProfileResponse createWorkerProfile(CreateWorkerProfileRequest request);

    WorkerProfileResponse getWorkerProfile(Long workerId);

    boolean existsByUserId(Long userId);
    WorkerProfileResponse getWorkerProfileByUserId(Long userId);

    WorkerProfileResponse updateWorkerProfile(
            Long workerId,
            UpdateWorkerProfileRequest request
    );
    WorkerProfileResponse updateVisibility(
            Long workerId,
            UpdateVisibilityRequest request
    );
    WorkerProfileResponse updateProfileStatus(
            Long workerId,
            UpdateProfileStatusRequest request
    );
    WorkerProfileResponse updateVerificationStatus(
            Long workerId,
            UpdateVerificationStatusRequest request
    );
    Page<WorkerProfileResponse> getAllWorkerProfiles(Pageable pageable);

    Page<WorkerProfileResponse> getWorkerProfilesByFilters(
            ProfileStatus profileStatus,
            VerificationStatus verificationStatus,
            Boolean discoverable,
            Pageable pageable
    );
}
