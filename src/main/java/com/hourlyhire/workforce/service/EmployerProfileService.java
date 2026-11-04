
package com.hourlyhire.workforce.service;

import com.hourlyhire.workforce.dto.request.CreateEmployerProfileRequest;
import com.hourlyhire.workforce.dto.request.UpdateEmployerProfileRequest;
import com.hourlyhire.workforce.dto.response.EmployerProfileResponse;
import com.hourlyhire.workforce.enums.EmployerType;
import com.hourlyhire.workforce.enums.ProfileStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployerProfileService {

    // Create employer profile
    EmployerProfileResponse createProfile(
            CreateEmployerProfileRequest request
    );

    // Get profile using employer profile ID
    EmployerProfileResponse getProfileById(Long employerId);

    // Get profile using user ID
    EmployerProfileResponse getProfileByUserId(Long userId);

    // Update employer profile
    EmployerProfileResponse updateProfile(
            Long employerId,
            UpdateEmployerProfileRequest request
    );

    // Activate or deactivate employer profile
    EmployerProfileResponse updateProfileStatus(
            Long employerId,
            ProfileStatus profileStatus
    );

    // Get all employer profiles with pagination
    Page<EmployerProfileResponse> getAllProfiles(Pageable pageable);

    // Get employer profiles using optional filters
    Page<EmployerProfileResponse> getFilteredProfiles(
            EmployerType employerType,
            ProfileStatus profileStatus,
            Pageable pageable
    );
}
