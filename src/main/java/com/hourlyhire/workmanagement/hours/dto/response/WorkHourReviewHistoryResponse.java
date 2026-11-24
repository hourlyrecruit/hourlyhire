
package com.hourlyhire.workmanagement.hours.dto.response;

import com.hourlyhire.workmanagement.hours.enums.WorkHourStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WorkHourReviewHistoryResponse(

        Long id,

        Long submissionId,

        Long employerId,

        BigDecimal submittedHours,

        BigDecimal approvedHours,

        WorkHourStatus status,

        String remarks,

        LocalDateTime reviewedAt

) {
}
