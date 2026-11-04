package com.hourlyhire.workforce.dto.request;

import com.hourlyhire.workforce.enums.VerificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateVerificationStatusRequest {
    @NotNull(message = "Verification status is required")
    private VerificationStatus verificationStatus;
}
