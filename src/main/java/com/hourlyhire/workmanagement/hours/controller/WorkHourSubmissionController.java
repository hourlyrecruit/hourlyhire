package com.hourlyhire.workmanagement.hours.controller;

import com.hourlyhire.workmanagement.hours.dto.request.WorkHourCorrectionRequest;
import com.hourlyhire.workmanagement.hours.dto.request.WorkHourReviewRequest;
import com.hourlyhire.workmanagement.hours.dto.request.WorkHourSubmissionRequest;
import com.hourlyhire.workmanagement.hours.dto.response.WorkHourReviewHistoryResponse;
import com.hourlyhire.workmanagement.hours.dto.response.WorkHourReviewResponse;
import com.hourlyhire.workmanagement.hours.dto.response.WorkHourSubmissionResponse;
import com.hourlyhire.workmanagement.hours.service.WorkHourSubmissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/work-hours")
@Tag(
        name = "10 Work Hours Management",
        description = "APIs for submitting, reviewing, approving, rejecting and correcting worker working hours"
)
public class WorkHourSubmissionController {

    private final WorkHourSubmissionService workHourSubmissionService;

    public WorkHourSubmissionController(
            WorkHourSubmissionService workHourSubmissionService) {
        this.workHourSubmissionService = workHourSubmissionService;
    }

    // Worker submits working hours
    @Operation(
            summary = "Submit working hours. Access: WORKER",
            description = "Allows an authenticated worker to submit working hours for an assigned booking."
    )
    @PreAuthorize("hasRole('WORKER')")
    @PostMapping("/submit")
    public ResponseEntity<WorkHourSubmissionResponse> submitHours(
            @Valid @RequestBody WorkHourSubmissionRequest request) {

        WorkHourSubmissionResponse response =
                workHourSubmissionService.submitHours(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Worker resubmits corrected working hours
    @Operation(
            summary = "Resubmit corrected hours. Access: WORKER",
            description = "Allows a worker to submit corrected hours when the employer requests corrections."
    )
    @PreAuthorize("hasRole('WORKER')")
    @PostMapping("/{submissionId}/resubmit")
    public ResponseEntity<WorkHourSubmissionResponse> resubmitCorrectedHours(
            @PathVariable Long submissionId,
            @Valid @RequestBody WorkHourCorrectionRequest request) {

        WorkHourSubmissionResponse response =
                workHourSubmissionService.resubmitCorrectedHours(
                        submissionId, request);

        return ResponseEntity.ok(response);
    }

    // Worker views personal submissions
    @Operation(
            summary = "Get my work hour submissions. Access: WORKER",
            description = "Retrieves all working-hour submissions belonging to the authenticated worker."
    )
    @PreAuthorize("hasRole('WORKER')")
    @GetMapping("/my-submissions")
    public ResponseEntity<List<WorkHourSubmissionResponse>> getMySubmissions() {

        return ResponseEntity.ok(
                workHourSubmissionService.getMySubmissions()
        );
    }

    // Employer views submissions for a booking
    @Operation(
            summary = "Get booking work hour submissions. Access: EMPLOYER",
            description = "Retrieves working-hour submissions for a booking owned by the authenticated employer."
    )
    @PreAuthorize("hasRole('EMPLOYER')")
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<WorkHourSubmissionResponse>> getBookingSubmissions(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                workHourSubmissionService.getBookingSubmissions(bookingId)
        );
    }

    // Employer approves working hours
    @Operation(
            summary = "Approve working hours. Access: EMPLOYER",
            description = "Allows the employer who owns the booking to approve the worker's submitted hours."
    )
    @PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/{submissionId}/approve")
    public ResponseEntity<WorkHourReviewResponse> approveHours(
            @PathVariable Long submissionId,
            @Valid @RequestBody WorkHourReviewRequest request) {

        WorkHourReviewResponse response =
                workHourSubmissionService.approveHours(
                        submissionId, request);

        return ResponseEntity.ok(response);
    }

    // Employer rejects working hours
    @Operation(
            summary = "Reject working hours. Access: EMPLOYER",
            description = "Allows the employer who owns the booking to reject the submitted working hours with remarks."
    )
    @PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/{submissionId}/reject")
    public ResponseEntity<WorkHourReviewResponse> rejectHours(
            @PathVariable Long submissionId,
            @RequestParam String employerRemarks) {

        WorkHourReviewResponse response =
                workHourSubmissionService.rejectHours(
                        submissionId, employerRemarks);

        return ResponseEntity.ok(response);
    }

    // Employer requests corrections
    @Operation(
            summary = "Request working hour corrections. Access: EMPLOYER",
            description = "Allows the employer who owns the booking to request corrections to submitted working hours."
    )
    @PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/{submissionId}/request-correction")
    public ResponseEntity<WorkHourReviewResponse> requestCorrection(
            @PathVariable Long submissionId,
            @RequestParam String employerRemarks) {

        WorkHourReviewResponse response =
                workHourSubmissionService.requestCorrection(
                        submissionId, employerRemarks);

        return ResponseEntity.ok(response);
    }

    // Worker or employer views review history
    @Operation(
            summary = "Get work hour review history. Access: WORKER, EMPLOYER",
            description = "Retrieves review history for a submission. Workers can access their own submissions, and employers can access submissions belonging to their bookings."
    )
    @PreAuthorize("hasAnyRole('WORKER', 'EMPLOYER')")
    @GetMapping("/{submissionId}/review-history")
    public ResponseEntity<List<WorkHourReviewHistoryResponse>> getReviewHistory(
            @PathVariable Long submissionId) {

        return ResponseEntity.ok(
                workHourSubmissionService.getReviewHistory(submissionId)
        );
    }
}
