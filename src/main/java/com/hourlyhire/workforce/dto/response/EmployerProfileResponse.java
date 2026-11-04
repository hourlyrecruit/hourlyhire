package com.hourlyhire.workforce.dto.response;

import com.hourlyhire.workforce.enums.EmployerType;
import com.hourlyhire.workforce.enums.ProfileStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EmployerProfileResponse {

    private Long id;
    private Long userId;
    private EmployerType employerType;
    private String displayName;
    private String companyLegalName;
    private String businessDescription;
    private String contactPerson;
    private String contactPhone;
    private String businessEmail;
    private String websiteUrl;
    private String businessLogoUrl;
    private ProfileStatus profileStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}