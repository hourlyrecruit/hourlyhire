
package com.hourlyhire.workforce.service.impl;

import com.hourlyhire.authentication.entity.UserAccount;
import com.hourlyhire.authentication.enums.Role;
import com.hourlyhire.authentication.repository.UserAccountRepository;

import com.hourlyhire.shared.enums.ErrorCode;
import com.hourlyhire.shared.exception.BusinessException;
import com.hourlyhire.shared.exception.ResourceNotFoundException;

import com.hourlyhire.workforce.dto.request.*;
import com.hourlyhire.workforce.dto.response.WorkerProfileResponse;
import com.hourlyhire.workforce.dto.response.WorkerPublicProfileResponse;
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

    private final UserAccountRepository userAccountRepository;
    private final WorkerProfileRepository workerProfileRepository;

    // 1. Create Worker Profile
    @Override
    @Transactional
    public WorkerProfileResponse createWorkerProfile(
            CreateWorkerProfileRequest request,
            Long userId) {

        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User account", userId.toString()));

        if (user.getRole() != Role.WORKER) {
            throw new BusinessException(
                    ErrorCode.ACCESS_DENIED,
                    "Only WORKER accounts can create worker profiles.");
        }

        if (workerProfileRepository.existsByUserAccount_Id(userId)) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "Worker profile already exists for this user.");
        }

        WorkerProfile profile = new WorkerProfile();
        profile.setUserAccount(user);
        profile.setBio(request.getBio());
        profile.setProfilePhotoUrl(request.getProfilePhotoUrl());

        WorkerProfile savedProfile =
                workerProfileRepository.save(profile);

        return mapToResponse(savedProfile);
    }

    // 2. Get Worker Profile by Profile ID
    @Override
    @Transactional(readOnly = true)
    public WorkerProfileResponse getWorkerProfile(Long workerId) {

        WorkerProfile profile = findProfileById(workerId);

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

        WorkerProfile profile = workerProfileRepository
                .findByUserAccount_Id(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Worker Profile", userId.toString()));

        return mapToResponse(profile);
    }

    // 5. Update Own Worker Profile

    // 5. Update Own Worker Profile
    @Override
    @Transactional
    public WorkerProfileResponse updateWorkerProfile(
            UpdateWorkerProfileRequest request,
            Long userId) {

        WorkerProfile profile = findProfileByUserId(userId);

        // Update bio only when it is provided
        if (request.getBio() != null) {
            profile.setBio(request.getBio().trim());
        }

        // Update profile photo only when it is provided
        if (request.getProfilePhotoUrl() != null) {
            profile.setProfilePhotoUrl(
                    request.getProfilePhotoUrl().trim()
            );
        }

        WorkerProfile updatedProfile =
                workerProfileRepository.save(profile);

        return mapToResponse(updatedProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkerPublicProfileResponse getPublicWorkerProfile(Long workerId) {

        WorkerProfile profile = workerProfileRepository.findById(workerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Worker Profile",
                                workerId.toString()
                        )
                );

        // Check whether the profile is active
        if (profile.getProfileStatus() != ProfileStatus.ACTIVE) {
            throw new ResourceNotFoundException(
                    "Worker Profile", workerId.toString()
            );
        }

        // Check whether the worker is verified
        if (profile.getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new ResourceNotFoundException(
                    "Worker Profile", workerId.toString()
            );
        }

        // Check whether the profile is discoverable
        if (!profile.isDiscoverable()) {
            throw new ResourceNotFoundException(
                    "Worker Profile", workerId.toString()
            );
        }

        UserAccount user = profile.getUserAccount();

        return WorkerPublicProfileResponse.builder()
                .id(profile.getId())
                .username(user.getUsername())
                .bio(profile.getBio())
                .profilePhotoUrl(profile.getProfilePhotoUrl())
                .verificationStatus(profile.getVerificationStatus())
                .build();
    }

    // 6. Update Own Profile Visibility
    @Override
    @Transactional
    public WorkerProfileResponse updateVisibility(
            UpdateVisibilityRequest request,
            Long userId) {

        WorkerProfile profile = findProfileByUserId(userId);

        profile.setDiscoverable(request.getDiscoverable());

        WorkerProfile updatedProfile =
                workerProfileRepository.save(profile);

        return mapToResponse(updatedProfile);
    }

    // 7. ADMIN: Update Profile Status
    @Override
    @Transactional
    public WorkerProfileResponse updateProfileStatus(
            Long workerId,
            UpdateProfileStatusRequest request) {

        WorkerProfile profile = findProfileById(workerId);

        profile.setProfileStatus(request.getProfileStatus());

        WorkerProfile updatedProfile =
                workerProfileRepository.save(profile);

        return mapToResponse(updatedProfile);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<WorkerPublicProfileResponse> getDiscoverableWorkers(
            Pageable pageable) {

        Page<WorkerProfile> profiles =
                workerProfileRepository.findByFilters(
                        ProfileStatus.ACTIVE,
                        VerificationStatus.VERIFIED,
                        true,
                        pageable
                );

        return profiles.map(this::mapToPublicResponse);
    }

    // 8. ADMIN: Update Verification Status
    @Override
    @Transactional
    public WorkerProfileResponse updateVerificationStatus(
            Long workerId,
            UpdateVerificationStatusRequest request) {

        WorkerProfile profile = findProfileById(workerId);

        profile.setVerificationStatus(
                request.getVerificationStatus());

        WorkerProfile updatedProfile =
                workerProfileRepository.save(profile);

        return mapToResponse(updatedProfile);
    }

    // 9. ADMIN: Get All Worker Profiles
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

    // Find profile using profile primary key
    private WorkerProfile findProfileById(Long workerId) {

        return workerProfileRepository.findById(workerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Worker Profile", workerId.toString()));
    }

    // Find profile using authenticated UserAccount ID
    private WorkerProfile findProfileByUserId(Long userId) {

        return workerProfileRepository
                .findByUserAccount_Id(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Worker Profile", userId.toString()));
    }

    // Convert Entity to Response DTO
    private WorkerProfileResponse mapToResponse(
            WorkerProfile workerProfile) {

        UserAccount user = workerProfile.getUserAccount();

        return WorkerProfileResponse.builder()
                .id(workerProfile.getId())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .bio(workerProfile.getBio())
                .profilePhotoUrl(workerProfile.getProfilePhotoUrl())
                .verificationStatus(
                        workerProfile.getVerificationStatus())
                .profileStatus(workerProfile.getProfileStatus())
                .discoverable(workerProfile.isDiscoverable())
                .createdAt(workerProfile.getCreatedAt())
                .updatedAt(workerProfile.getUpdatedAt())
                .build();
    }
    private WorkerPublicProfileResponse mapToPublicResponse(
            WorkerProfile profile) {

        UserAccount user = profile.getUserAccount();

        return WorkerPublicProfileResponse.builder()
                .id(profile.getId())
                .username(user.getUsername())
                .bio(profile.getBio())
                .profilePhotoUrl(profile.getProfilePhotoUrl())
                .verificationStatus(profile.getVerificationStatus())
                .build();
    }
}
