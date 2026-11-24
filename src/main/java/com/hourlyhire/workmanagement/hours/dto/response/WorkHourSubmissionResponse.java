package com.hourlyhire.workmanagement.hours.dto.response;

import com.hourlyhire.workmanagement.hours.enums.WorkHourStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WorkHourSubmissionResponse(

        Long id,

        Long bookingId,

        Long workerId,

        BigDecimal submittedHours,

        BigDecimal approvedHours,

        WorkHourStatus status,

        String workerRemarks,

        String employerRemarks,

        LocalDateTime submittedAt,

        LocalDateTime reviewedAt

) {
}
