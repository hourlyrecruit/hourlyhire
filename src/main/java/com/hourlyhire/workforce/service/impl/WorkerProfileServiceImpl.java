package com.hourlyhire.workforce.service.impl;

import com.hourlyhire.authentication.entity.UserAccount;
import com.hourlyhire.authentication.enums.Role;
import com.hourlyhire.authentication.repository.UserAccountRepository;
import com.hourlyhire.security.jwt.AuthenticatedUser;
import com.hourlyhire.security.utils.SecurityUtils;
import com.hourlyhire.shared.enums.ErrorCode;
import com.hourlyhire.shared.exception.BusinessException;
import com.hourlyhire.shared.exception.ResourceNotFoundException;
import com.hourlyhire.workforce.dto.request.*;
import com.hourlyhire.workforce.dto.response.WorkerProfileResponse;
import com.hourlyhire.workforce.entity.WorkerProfile;
import com.hourlyhire.workforce.enums.ProfileStatus;
import com.hourlyhire.workforce.enums.VerificationStatus;
import com.hourlyhire.workforce.repository.WorkerProfileRepository;
import com.hourlyhire.workforce.service.WorkerProfileService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkerProfileServiceImpl implements WorkerProfileService {

    AuthenticatedUser user = SecurityUtils.getCurrentUser();
    private final UserAccountRepository userAccountRepository;
    private final WorkerProfileRepository workerProfileRepository;

    // 1. Create Worker Profile
    @Override
    @Transactional
    public WorkerProfileResponse createWorkerProfile(
            CreateWorkerProfileRequest request) {

        Long userId = request.getUserId();

        // Check whether UserAccount exists
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "UserAccount", userId.toString()
                        ));

        // Validate account role
        if (userAccount.getRole() != Role.WORKER) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "Only WORKER accounts can create worker profiles."
            );
        }

        // Check duplicate profile
        if (workerProfileRepository.existsByUserAccount_Id(userId)) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "Worker profile already exists for this user."
            );
        }

        WorkerProfile profile = new WorkerProfile();

        // Establish UserAccount relationship
        profile.setUserAccount(userAccount);
        profile.setBio(request.getBio());
        profile.setProfilePhotoUrl(request.getProfilePhotoUrl());

        WorkerProfile savedProfile =
                workerProfileRepository.save(profile);

        return mapToResponse(savedProfile);
    }

    // 2. Get Worker Profile by Worker ID
    @Override
    @Transactional(readOnly = true)
    public WorkerProfileResponse getWorkerProfile(Long workerId) {

        WorkerProfile profile = workerProfileRepository.findById(workerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Worker Profile", workerId.toString()
                        ));

        return mapToResponse(profile);
    }

    // 3. Check Worker Profile by User ID
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserId(Long userId) {

        return workerProfileRepository
                .existsByUserAccount_Id(userId);
    }

    // 4. Get Worker Profile by User ID
    @Override
    @Transactional(readOnly = true)
    public WorkerProfileResponse getWorkerProfileByUserId(Long userId) {

        WorkerProfile workerProfile = workerProfileRepository
                .findByUserAccount_Id(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Worker Profile", userId.toString()
                        ));

        return mapToResponse(workerProfile);
    }

    // 5. Update Worker Profile
    @Override
    @Transactional
    public WorkerProfileResponse updateWorkerProfile(
            Long workerId,
            UpdateWorkerProfileRequest request) {

        WorkerProfile workerProfile = workerProfileRepository
                .findById(workerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Worker Profile", workerId.toString()
                        ));

        workerProfile.setBio(request.getBio());
        workerProfile.setProfilePhotoUrl(request.getProfilePhotoUrl());

        WorkerProfile updatedProfile =
                workerProfileRepository.save(workerProfile);

        return mapToResponse(updatedProfile);
    }

    // 6. Update Visibility
    @Override
    @Transactional
    public WorkerProfileResponse updateVisibility(
            Long workerId,
            UpdateVisibilityRequest request) {

        WorkerProfile workerProfile = workerProfileRepository
                .findById(workerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Worker Profile", workerId.toString()
                        ));

        workerProfile.setDiscoverable(request.getDiscoverable());

        WorkerProfile updatedProfile =
                workerProfileRepository.save(workerProfile);

        return mapToResponse(updatedProfile);
    }

    // 7. Update Profile Status
    @Override
    @Transactional
    public WorkerProfileResponse updateProfileStatus(
            Long workerId,
            UpdateProfileStatusRequest request) {

        WorkerProfile workerProfile = workerProfileRepository
                .findById(workerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Worker Profile", workerId.toString()
                        ));

        workerProfile.setProfileStatus(request.getProfileStatus());

        WorkerProfile updatedProfile =
                workerProfileRepository.save(workerProfile);

        return mapToResponse(updatedProfile);
    }

    // 8. Update Verification Status
    @Override
    @Transactional
    public WorkerProfileResponse updateVerificationStatus(
            Long workerId,
            UpdateVerificationStatusRequest request) {

        WorkerProfile workerProfile = workerProfileRepository
                .findById(workerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Worker Profile", workerId.toString()
                        ));

        workerProfile.setVerificationStatus(
                request.getVerificationStatus()
        );

        WorkerProfile updatedProfile =
                workerProfileRepository.save(workerProfile);

        return mapToResponse(updatedProfile);
    }

    // 9. Get All Worker Profiles
    @Override
    @Transactional(readOnly = true)
    public Page<WorkerProfileResponse> getAllWorkerProfiles(
            Pageable pageable) {

        Page<WorkerProfile> profiles =
                workerProfileRepository.findAll(pageable);

        return profiles.map(this::mapToResponse);
    }

    // 10. Get Worker Profiles by Filters
    @Override
    @Transactional(readOnly = true)
    public Page<WorkerProfileResponse> getWorkerProfilesByFilters(
            ProfileStatus profileStatus,
            VerificationStatus verificationStatus,
            Boolean discoverable,
            Pageable pageable) {

        Page<WorkerProfile> profiles =
                workerProfileRepository.findByFilters(
                        profileStatus,
                        verificationStatus,
                        discoverable,
                        pageable
                );

        return profiles.map(this::mapToResponse);
    }

    // Convert WorkerProfile entity into response DTO
    private WorkerProfileResponse mapToResponse(WorkerProfile profile) {

        return WorkerProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserAccount().getId())
                .bio(profile.getBio())
                .profilePhotoUrl(profile.getProfilePhotoUrl())
                .verificationStatus(profile.getVerificationStatus())
                .profileStatus(profile.getProfileStatus())
                .discoverable(profile.isDiscoverable())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
