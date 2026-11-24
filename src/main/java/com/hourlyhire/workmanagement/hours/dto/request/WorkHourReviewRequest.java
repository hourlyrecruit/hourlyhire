
package com.hourlyhire.workmanagement.hours.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record WorkHourReviewRequest(

        @NotNull(message = "Approved hours are required")
        @DecimalMin(value = "0.01", message = "Approved hours must be greater than zero")
        BigDecimal approvedHours,

        @Size(max = 1000, message = "Employer remarks cannot exceed 1000 characters")
        String employerRemarks

) {
}
