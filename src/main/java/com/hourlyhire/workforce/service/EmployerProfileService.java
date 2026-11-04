package com.hourlyhire.workforce.service;

import com.hourlyhire.authentication.enums.Role;

import com.hourlyhire.workforce.dto.request.CreateEmployerProfileRequest;
import com.hourlyhire.workforce.dto.request.UpdateEmployerProfileRequest;
import com.hourlyhire.workforce.dto.response.EmployerProfileResponse;
import com.hourlyhire.workforce.enums.EmployerType;
import com.hourlyhire.workforce.enums.ProfileStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployerProfileService {

    // 1. Create employer profile
    EmployerProfileResponse createProfile(
            CreateEmployerProfileRequest request,
            Long targetUserId
    );

    // 2. Get profile by employer profile ID with access validation
    EmployerProfileResponse getProfileById(
            Long employerId,
            Long requesterId,
            Role requesterRole
    );

    // 3. Get profile by user ID with ownership validation
    EmployerProfileResponse getProfileByUserId(
            Long userId,
            Long requesterId,
            Role requesterRole
    );

    // 4. Update employer profile with ownership validation
    EmployerProfileResponse updateProfile(
            Long employerId,
            UpdateEmployerProfileRequest request,
            Long requesterId,
            Role requesterRole
    );

    // 5. Activate or deactivate employer profile
    EmployerProfileResponse updateProfileStatus(
            Long employerId,
            ProfileStatus profileStatus
    );

    // 6. Get all employer profiles with pagination
    Page<EmployerProfileResponse> getAllProfiles(Pageable pageable);

    // 7. Get employer profiles using optional filters and role validation
    Page<EmployerProfileResponse> getFilteredProfiles(
            EmployerType employerType,
            ProfileStatus profileStatus,
            Pageable pageable,
            Role requesterRole
    );
}