package com.hourlyhire.workmanagement.hours.service;

import com.hourlyhire.workmanagement.hours.dto.request.WorkHourCorrectionRequest;
import com.hourlyhire.workmanagement.hours.dto.request.WorkHourReviewRequest;
import com.hourlyhire.workmanagement.hours.dto.request.WorkHourSubmissionRequest;
import com.hourlyhire.workmanagement.hours.dto.response.WorkHourReviewHistoryResponse;
import com.hourlyhire.workmanagement.hours.dto.response.WorkHourReviewResponse;
import com.hourlyhire.workmanagement.hours.dto.response.WorkHourSubmissionResponse;

import java.util.List;

public interface WorkHourSubmissionService {

    // Worker submits working hours
    WorkHourSubmissionResponse submitHours(
            WorkHourSubmissionRequest request
    );

    // Worker views their submitted hours
    List<WorkHourSubmissionResponse> getMySubmissions();

    // Employer views submissions for a booking
    List<WorkHourSubmissionResponse> getBookingSubmissions(
            Long bookingId
    );

    // Employer approves submitted hours
    WorkHourReviewResponse approveHours(
            Long submissionId,
            WorkHourReviewRequest request
    );

    // Employer rejects submitted hours
    WorkHourReviewResponse rejectHours(
            Long submissionId,
            String employerRemarks
    );

    // Employer requests corrections
    WorkHourReviewResponse requestCorrection(
            Long submissionId,
            String employerRemarks
    );
    // Worker resubmits corrected working hours
    WorkHourSubmissionResponse resubmitCorrectedHours(
            Long submissionId,
            WorkHourCorrectionRequest request
    );
    // Retrieve review history for a submission
    List<WorkHourReviewHistoryResponse> getReviewHistory(
            Long submissionId
    );
}
