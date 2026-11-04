
package com.hourlyhire.workforce.service.impl;

import com.hourlyhire.authentication.entity.UserAccount;
import com.hourlyhire.authentication.enums.Role;
import com.hourlyhire.authentication.repository.UserAccountRepository;

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
    private final UserAccountRepository userAccountRepository;

    // 1. Create employer profile
    @Override
    public EmployerProfileResponse createProfile(
            CreateEmployerProfileRequest request,
            Long targetUserId) {

        // Validate target user ID
        if (targetUserId == null) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "Target user ID is required."
            );
        }

        // Find UserAccount
        UserAccount userAccount = userAccountRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "UserAccount",
                        targetUserId.toString()
                ));

        // Validate account role
        if (userAccount.getRole() != Role.EMPLOYER) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "Only users with EMPLOYER role can create employer profiles."
            );
        }

        // Check duplicate profile
        if (employerProfileRepository.existsByUserAccount_Id(targetUserId)) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_FAILED,
                    "Employer profile already exists for this user."
            );
        }

        // Create employer profile
        EmployerProfile employer = new EmployerProfile();

        employer.setUserAccount(userAccount);
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


    // 2. Get employer profile by profile ID
    @Override
    @Transactional(readOnly = true)
    public EmployerProfileResponse getProfileById(
            Long employerId,
            Long requesterId,
            Role requesterRole) {

        EmployerProfile employer = findEmployerById(employerId);

        validateProfileAccess(employer, requesterId, requesterRole);

        return mapToResponse(employer);
    }
    // 4. Update employer profile
    @Override
    public EmployerProfileResponse updateProfile(
            Long employerId,
            UpdateEmployerProfileRequest request,
            Long requesterId,
            Role requesterRole) {

        EmployerProfile employer = findEmployerById(employerId);

        validateProfileAccess(employer, requesterId, requesterRole);

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

    // 5. Activate or deactivate employer profile
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
            Pageable pageable,
            Role requesterRole) {

        // Workers can view only ACTIVE employer profiles
        if (requesterRole == Role.WORKER) {
            profileStatus = ProfileStatus.ACTIVE;
        }

        return employerProfileRepository
                .findByFilters(employerType, profileStatus, pageable)
                .map(this::mapToResponse);
    }
    // Get employer profile by UserAccount ID
    @Override
    @Transactional(readOnly = true)
    public EmployerProfileResponse getProfileByUserId(
            Long userId,
            Long requesterId,
            Role requesterRole) {

        EmployerProfile employer = employerProfileRepository
                .findByUserAccount_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employer profile",
                        userId.toString()
                ));

        validateProfileAccess(employer, requesterId, requesterRole);

        return mapToResponse(employer);
    }

    // Find employer profile or throw exception
    private EmployerProfile findEmployerById(Long employerId) {

        return employerProfileRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employer profile",
                        employerId.toString()
                ));
    }

    // Validate employer profile ownership and access
    private void validateProfileAccess(
            EmployerProfile employer,
            Long requesterId,
            Role requesterRole) {

        // ADMIN can access all employer profiles
        if (requesterRole == Role.ADMIN) {
            return;
        }

        // EMPLOYER can access only their own profile
        if (requesterRole == Role.EMPLOYER) {

            Long ownerId = employer.getUserAccount().getId();

            if (!ownerId.equals(requesterId)) {
                throw new BusinessException(
                        ErrorCode.ACCESS_DENIED,
                        "You can access only your own employer profile."
                );
            }

            return;
        }

        // WORKER can view only active employer profiles
        if (requesterRole == Role.WORKER) {

            if (employer.getProfileStatus() != ProfileStatus.ACTIVE) {
                throw new BusinessException(
                        ErrorCode.ACCESS_DENIED,
                        "This employer profile is not available."
                );
            }

            return;
        }

        throw new BusinessException(
                ErrorCode.ACCESS_DENIED,
                "You are not authorized to access this employer profile."
        );
    }

    // Convert EmployerProfile entity into response DTO
    private EmployerProfileResponse mapToResponse(
            EmployerProfile employer) {

        return EmployerProfileResponse.builder()
                .id(employer.getId())
                .userId(employer.getUserAccount().getId())
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
