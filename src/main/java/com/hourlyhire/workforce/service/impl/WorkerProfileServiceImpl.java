
package com.hourlyhire.workforce.service.impl;

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

    private final WorkerProfileRepository workerProfileRepository;

    @Override
    @Transactional
    public WorkerProfileResponse createWorkerProfile(
            CreateWorkerProfileRequest request) {

        if (workerProfileRepository.existsByUserId(request.getUserId())) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "Worker profile already exists for this user."
            );
        }
        WorkerProfile profile = new WorkerProfile();

        profile.setUserId(request.getUserId());
        profile.setBio(request.getBio());
        profile.setProfilePhotoUrl(request.getProfilePhotoUrl());

        WorkerProfile savedProfile =
                workerProfileRepository.save(profile);

        return  WorkerProfileResponse.builder()
                .id(savedProfile.getId())
                .userId(savedProfile.getUserId())
                .bio(savedProfile.getBio())
                .profilePhotoUrl(savedProfile.getProfilePhotoUrl())
                .verificationStatus(savedProfile.getVerificationStatus())
                .profileStatus(savedProfile.getProfileStatus())
                .discoverable(savedProfile.isDiscoverable())
                .createdAt(savedProfile.getCreatedAt())
                .updatedAt(savedProfile.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkerProfileResponse getWorkerProfile(Long workerId) {

        WorkerProfile profile = workerProfileRepository.findById(workerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Worker Profile",
                                workerId.toString()
                        ));

        return mapToResponse(profile);
    }


    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserId(Long userId) {

        return workerProfileRepository.existsByUserId(userId);
    }

    @Override
    public WorkerProfileResponse getWorkerProfileByUserId(Long userId) {
        WorkerProfile workerProfile = workerProfileRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Worker Profile", userId.toString()
                        )
                );
        return WorkerProfileResponse.builder()
                .id(workerProfile.getId())
                .userId(workerProfile.getUserId())
                .bio(workerProfile.getBio())
                .profilePhotoUrl(workerProfile.getProfilePhotoUrl())
                .verificationStatus(workerProfile.getVerificationStatus())
                .profileStatus(workerProfile.getProfileStatus())
                .discoverable(workerProfile.isDiscoverable())
                .createdAt(workerProfile.getCreatedAt())
                .updatedAt(workerProfile.getUpdatedAt())
                .build();
    }

    @Override
    public WorkerProfileResponse updateWorkerProfile(Long workerId, UpdateWorkerProfileRequest request) {
        WorkerProfile workerProfile = workerProfileRepository
                .findById(workerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Worker Profile", workerId.toString()
                        )
                );

        workerProfile.setBio(request.getBio());
        workerProfile.setProfilePhotoUrl(request.getProfilePhotoUrl());

        WorkerProfile updatedProfile =
                workerProfileRepository.save(workerProfile);

        return WorkerProfileResponse.builder()
                .id(updatedProfile.getId())
                .userId(updatedProfile.getUserId())
                .bio(updatedProfile.getBio())
                .profilePhotoUrl(updatedProfile.getProfilePhotoUrl())
                .verificationStatus(updatedProfile.getVerificationStatus())
                .profileStatus(updatedProfile.getProfileStatus())
                .discoverable(updatedProfile.isDiscoverable())
                .createdAt(updatedProfile.getCreatedAt())
                .updatedAt(updatedProfile.getUpdatedAt())
                .build();
    }

    @Override
    public WorkerProfileResponse updateVisibility(
              Long workerId, UpdateVisibilityRequest request) {
        WorkerProfile workerProfile = workerProfileRepository
                .findById(workerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Worker Profile",
                                workerId.toString()
                        )
                );
        workerProfile.setDiscoverable(request.getDiscoverable());
        WorkerProfile updatedProfile = workerProfileRepository.save(workerProfile);
        return WorkerProfileResponse.builder()
                .id(updatedProfile.getId())
                .userId(updatedProfile.getUserId())
                .bio(updatedProfile.getBio())
                .profilePhotoUrl(updatedProfile.getProfilePhotoUrl())
                .verificationStatus(updatedProfile.getVerificationStatus())
                .profileStatus(updatedProfile.getProfileStatus())
                .discoverable(updatedProfile.isDiscoverable())
                .createdAt(updatedProfile.getCreatedAt())
                .updatedAt(updatedProfile.getUpdatedAt())
                .build();
    }

    private WorkerProfileResponse mapToResponse(WorkerProfile profile) {

        return WorkerProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .bio(profile.getBio())
                .profilePhotoUrl(profile.getProfilePhotoUrl())
                .verificationStatus(profile.getVerificationStatus())
                .profileStatus(profile.getProfileStatus())
                .discoverable(profile.isDiscoverable())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
    @Override
    @Transactional
    public WorkerProfileResponse updateProfileStatus(
            Long workerId,
            UpdateProfileStatusRequest request) {

        // 1. Find worker profile using worker ID
        WorkerProfile workerProfile = workerProfileRepository
                .findById(workerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Worker Profile",
                                workerId.toString()
                        )
                );

        // 2. Update profile status
        workerProfile.setProfileStatus(request.getProfileStatus());

        WorkerProfile updatedProfile =
                workerProfileRepository.save(workerProfile);

        // Return updated response
        return mapToResponse(updatedProfile);
    }

    @Override
    @Transactional
    public WorkerProfileResponse updateVerificationStatus(
            Long workerId,
            UpdateVerificationStatusRequest request) {

        // 1. Find worker profile
        WorkerProfile workerProfile = workerProfileRepository
                .findById(workerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Worker Profile",
                                workerId.toString()
                        )
                );

        // 2. Update verification status
        workerProfile.setVerificationStatus(
                request.getVerificationStatus()
        );

        // 3. Save updated profile
        WorkerProfile updatedProfile =
                workerProfileRepository.save(workerProfile);

        // 4. Convert entity into response DTO
        return mapToResponse(updatedProfile);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<WorkerProfileResponse> getAllWorkerProfiles(Pageable pageable) {

        Page<WorkerProfile> profiles =
                workerProfileRepository.findAll(pageable);

        return profiles.map(this::mapToResponse);
    }
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

}
