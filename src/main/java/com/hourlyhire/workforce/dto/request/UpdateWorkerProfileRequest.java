package com.hourlyhire.workforce.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateWorkerProfileRequest {

    @Size(
            max = 2000,
            message = "Bio must not exceed 2000 characters"
    )
    @Pattern(
            regexp = ".*\\S.*",
            message = "Bio must not be blank"
    )
    private String bio;

    @Size(
            max = 500,
            message = "Profile photo URL must not exceed 500 characters"
    )
    private String profilePhotoUrl;

    @AssertTrue(
            message = "At least one field must be provided for update"
    )
    public boolean isAtLeastOneFieldProvided() {
        return bio != null || profilePhotoUrl != null;
    }
}
