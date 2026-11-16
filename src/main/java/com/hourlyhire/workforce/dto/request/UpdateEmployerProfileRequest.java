
package com.hourlyhire.workforce.dto.request;

import com.hourlyhire.workforce.enums.EmployerType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEmployerProfileRequest {
    // Used by ADMIN when creating a profile for another employer
    private Long userId;

    @NotNull(message = "Employer type is required")
    private EmployerType employerType;

    @Size(max = 150, message = "Display name cannot exceed 150 characters")
    private String displayName;

    @Size(max = 200, message = "Company legal name cannot exceed 200 characters")
    private String companyLegalName;

    @Size(max = 5000, message = "Business description cannot exceed 5000 characters")
    private String businessDescription;

    @Size(max = 150)
    private String contactPerson;

    @Size(max = 20)
    private String contactPhone;

    @Email(message = "Enter a valid business email")
    @Size(max = 254)
    private String businessEmail;

    @Size(max = 500)
    private String websiteUrl;

    @Size(max = 500)
    private String businessLogoUrl;
}
