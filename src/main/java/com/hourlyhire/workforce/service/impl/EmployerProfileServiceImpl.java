package com.hourlyhire.workforce.service.impl;

import com.hourlyhire.workforce.dto.request.CreateEmployerProfileRequest;
import com.hourlyhire.workforce.dto.request.UpdateEmployerProfileRequest;
import com.hourlyhire.workforce.dto.response.EmployerProfileResponse;
import com.hourlyhire.workforce.entity.EmployerProfile;
import com.hourlyhire.workforce.enums.EmployerType;
import com.hourlyhire.workforce.enums.ProfileStatus;
import com.hourlyhire.workforce.repository.EmployerProfileRepository;
import com.hourlyhire.workforce.service.EmployerProfileService;

import com.hourlyhire.shared.exception.ResourceNotFoundException;
import com.hourlyhire.shared.exception.BusinessException;
import com.hourlyhire.shared.enums.ErrorCode;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployerProfileServiceImpl implements EmployerProfileService {

    private final EmployerProfileRepository employerProfileRepository;

    // 1. Create employer profile
    @Override
    public EmployerProfileResponse createProfile(
            CreateEmployerProfileRequest request) {

        if (employerProfileRepository.existsByUserId(request.getUserId())) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "Employer profile already exists for this user."
            );
        }

        EmployerProfile employer = new EmployerProfile();

        employer.setUserId(request.getUserId());
        employer.setEmployerType(request.getEmployerType());
        employer.setDisplayName(request.getDisplayName());
        employer.setCompanyLegalName(request.getCompanyLegalName());
        employer.setBusinessDescription(request.getBusinessDescription());
        employer.setContactPerson(request.getContactPerson());
        employer.setContactPhone(request.getContactPhone());
        employer.setBusinessEmail(request.getBusinessEmail());
        employer.setWebsiteUrl(request.getWebsiteUrl());
        employer.setBusinessLogoUrl(request.getBusinessLogoUrl());

        employer.setProfileStatus(ProfileStatus.ACTIVE);

        EmployerProfile saved = employerProfileRepository.save(employer);

        return mapToResponse(saved);
    }

    // 2. Get profile by employer ID
    @Override
    @Transactional(readOnly = true)
    public EmployerProfileResponse getProfileById(Long employerId) {

        EmployerProfile employer = findEmployerById(employerId);

        return mapToResponse(employer);
    }

    // 3. Get profile by user ID
    @Override
    @Transactional(readOnly = true)
    public EmployerProfileResponse getProfileByUserId(Long userId) {

        EmployerProfile employer = employerProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employer profile",
                        userId.toString()
                ));

        return mapToResponse(employer);
    }

    // 4. Update employer profile
    @Override
    public EmployerProfileResponse updateProfile(
            Long employerId,
            UpdateEmployerProfileRequest request) {

        EmployerProfile employer = findEmployerById(employerId);

        if (request.getDisplayName() != null) {
            employer.setDisplayName(request.getDisplayName());
        }

        if (request.getCompanyLegalName() != null) {
            employer.setCompanyLegalName(request.getCompanyLegalName());
        }

        if (request.getBusinessDescription() != null) {
            employer.setBusinessDescription(
                    request.getBusinessDescription()
            );
        }

        if (request.getContactPerson() != null) {
            employer.setContactPerson(request.getContactPerson());
        }

        if (request.getContactPhone() != null) {
            employer.setContactPhone(request.getContactPhone());
        }

        if (request.getBusinessEmail() != null) {
            employer.setBusinessEmail(request.getBusinessEmail());
        }

        if (request.getWebsiteUrl() != null) {
            employer.setWebsiteUrl(request.getWebsiteUrl());
        }

        if (request.getBusinessLogoUrl() != null) {
            employer.setBusinessLogoUrl(request.getBusinessLogoUrl());
        }

        EmployerProfile updated = employerProfileRepository.save(employer);

        return mapToResponse(updated);
    }

    // 5. Activate or deactivate profile
    @Override
    public EmployerProfileResponse updateProfileStatus(
            Long employerId,
            ProfileStatus profileStatus) {

        EmployerProfile employer = findEmployerById(employerId);

        employer.setProfileStatus(profileStatus);

        EmployerProfile updated = employerProfileRepository.save(employer);

        return mapToResponse(updated);
    }

    // 6. Get all employer profiles
    @Override
    @Transactional(readOnly = true)
    public Page<EmployerProfileResponse> getAllProfiles(Pageable pageable) {

        return employerProfileRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    // 7. Get filtered employer profiles
    @Override
    @Transactional(readOnly = true)
    public Page<EmployerProfileResponse> getFilteredProfiles(
            EmployerType employerType,
            ProfileStatus profileStatus,
            Pageable pageable) {

        return employerProfileRepository
                .findByFilters(employerType, profileStatus, pageable)
                .map(this::mapToResponse);
    }

    // Find employer or throw exception
    private EmployerProfile findEmployerById(Long employerId) {

        return employerProfileRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employer profile",
                        employerId.toString()
                ));
    }

    // Convert entity into response DTO
    private EmployerProfileResponse mapToResponse(
            EmployerProfile employer) {

        return EmployerProfileResponse.builder()
                .id(employer.getId())
                .userId(employer.getUserId())
                .employerType(employer.getEmployerType())
                .displayName(employer.getDisplayName())
                .companyLegalName(employer.getCompanyLegalName())
                .businessDescription(employer.getBusinessDescription())
                .contactPerson(employer.getContactPerson())
                .contactPhone(employer.getContactPhone())
                .businessEmail(employer.getBusinessEmail())
                .websiteUrl(employer.getWebsiteUrl())
                .businessLogoUrl(employer.getBusinessLogoUrl())
                .profileStatus(employer.getProfileStatus())
                .createdAt(employer.getCreatedAt())
                .updatedAt(employer.getUpdatedAt())
                .build();
    }
}
