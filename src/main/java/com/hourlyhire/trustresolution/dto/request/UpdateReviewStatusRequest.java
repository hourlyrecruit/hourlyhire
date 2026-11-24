package com.hourlyhire.trustresolution.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateReviewStatusRequest {

    @NotBlank(message = "Status is required")
    @Pattern(
        regexp = "ACTIVE|HIDDEN",
        message = "Status must be ACTIVE or HIDDEN"
    )
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}