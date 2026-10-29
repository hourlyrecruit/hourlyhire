package com.hourlyhire.workforce.service.impl;

import com.hourlyhire.workforce.dto.request.CreateWorkerProfileRequest;
import com.hourlyhire.workforce.dto.response.WorkerProfileResponse;
import com.hourlyhire.workforce.entity.WorkerProfile;
import com.hourlyhire.workforce.repository.WorkerProfileRepository;
import com.hourlyhire.workforce.service.WorkerProfileService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkerProfileServiceImpl  implements WorkerProfileService {

    private WorkerProfileRepository workerProfileRepository;


    @Override
    @Transactional
    public WorkerProfileResponse createWorkerProfile(
            CreateWorkerProfileRequest request) {
        if(workerProfileRepository.existsByUserId(request.getUserId()))
        {
            throw new IllegalArgumentException(
                    "A Worker profile Already exists for this user");
        }
        WorkerProfile profile = new WorkerProfile();
        profile.setUserId(request.getUserId());
        profile.setBio(request.getBio());
        profile.setProfilePhotoUrl(request.getProfilePhotoUrl());
        WorkerProfile savedProfile = workerProfileRepository.save(profile);
        return mapToResponse(savedProfile);

    }

    @Override
    @Transactional(readOnly = true)
    public WorkerProfileResponse getWorkerProfile(Long workerId) {
        WorkerProfile profile = workerProfileRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Worker profile not found with ID :" + workerId));

        return mapToResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserId(Long userId) {

        return workerProfileRepository.existsByUserId(userId);
    }

    private WorkerProfileResponse mapToResponse(WorkerProfile profile)
    {
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
}
