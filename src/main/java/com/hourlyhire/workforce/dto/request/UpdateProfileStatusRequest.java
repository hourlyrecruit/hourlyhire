package com.hourlyhire.workforce.dto.request;

import com.hourlyhire.workforce.enums.ProfileStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileStatusRequest {
    @NotNull(message = "Profile status is required")
    private ProfileStatus profileStatus;
}
