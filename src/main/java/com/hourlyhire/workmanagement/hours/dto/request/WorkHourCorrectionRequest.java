
package com.hourlyhire.workmanagement.hours.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record WorkHourCorrectionRequest(

        @NotNull(message = "Corrected hours are required")
        @DecimalMin(
                value = "0.01",
                message = "Corrected hours must be greater than zero"
        )
        BigDecimal correctedHours,

        @Size(
                max = 1000,
                message = "Worker remarks cannot exceed 1000 characters"
        )
        String workerRemarks

) {
}
