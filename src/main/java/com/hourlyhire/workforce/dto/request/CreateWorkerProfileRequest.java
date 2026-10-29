package com.hourlyhire.workforce.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWorkerProfileRequest {

    @NotNull(message = "user ID is required")
    private Long userId;

    @Size(max = 2000, message = "Bio must not exceed 2000 characters")
    private String bio;

    @Size(max = 500, message = "Profile photo URL must not exceed 500 characters")
    private String profilePhotoUrl;
}
