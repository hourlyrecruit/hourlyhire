
package com.hourlyhire.workmanagement.hours.dto.response;

import com.hourlyhire.workmanagement.hours.enums.WorkHourStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WorkHourReviewResponse(

        Long submissionId,

        Long bookingId,

        BigDecimal submittedHours,

        BigDecimal approvedHours,

        WorkHourStatus status,

        String employerRemarks,

        LocalDateTime reviewedAt

) {
}
