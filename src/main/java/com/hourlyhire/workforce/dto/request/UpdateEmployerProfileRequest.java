package com.hourlyhire.workforce.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEmployerProfileRequest {

    @Size(max = 150, message = "Display name cannot exceed 150 characters")
    private String displayName;

    @Size(max = 200, message = "Company legal name cannot exceed 200 characters")
    private String companyLegalName;

    @Size(max = 2000, message = "Business description cannot exceed 2000 characters")
    private String businessDescription;

    @Size(max = 100, message = "Contact person cannot exceed 100 characters")
    private String contactPerson;

    @Size(max = 20, message = "Contact phone cannot exceed 20 characters")
    private String contactPhone;

    @Email(message = "Please provide a valid business email")
    @Size(max = 150, message = "Business email cannot exceed 150 characters")
    private String businessEmail;

    @Size(max = 255, message = "Website URL cannot exceed 255 characters")
    private String websiteUrl;

    @Size(max = 500, message = "Business logo URL cannot exceed 500 characters")
    private String businessLogoUrl;
}