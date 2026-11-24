
package com.hourlyhire.workmanagement.hours.service.impl;

import com.hourlyhire.authentication.enums.Role;
import com.hourlyhire.security.jwt.AuthenticatedUser;
import com.hourlyhire.workforce.entity.EmployerProfile;
import com.hourlyhire.workforce.entity.WorkerProfile;
import com.hourlyhire.workforce.repository.EmployerProfileRepository;
import com.hourlyhire.workforce.repository.WorkerProfileRepository;
import com.hourlyhire.workmanagement.booking.entity.Booking;
import com.hourlyhire.workmanagement.booking.repository.BookingRepository;
import com.hourlyhire.workmanagement.hours.dto.request.WorkHourCorrectionRequest;
import com.hourlyhire.workmanagement.hours.dto.request.WorkHourReviewRequest;
import com.hourlyhire.workmanagement.hours.dto.request.WorkHourSubmissionRequest;
import com.hourlyhire.workmanagement.hours.dto.response.WorkHourReviewHistoryResponse;
import com.hourlyhire.workmanagement.hours.dto.response.WorkHourReviewResponse;
import com.hourlyhire.workmanagement.hours.dto.response.WorkHourSubmissionResponse;
import com.hourlyhire.workmanagement.hours.entity.WorkHourReviewHistory;
import com.hourlyhire.workmanagement.hours.entity.WorkHourSubmission;
import com.hourlyhire.workmanagement.hours.enums.WorkHourStatus;
import com.hourlyhire.workmanagement.hours.repository.WorkHourReviewHistoryRepository;
import com.hourlyhire.workmanagement.hours.repository.WorkHourSubmissionRepository;
import com.hourlyhire.workmanagement.hours.service.WorkHourSubmissionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WorkHourSubmissionServiceImpl
        implements WorkHourSubmissionService {
    private final WorkHourReviewHistoryRepository reviewHistoryRepository;
    private final WorkHourSubmissionRepository submissionRepository;
    private final BookingRepository bookingRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final EmployerProfileRepository employerProfileRepository;
    public WorkHourSubmissionServiceImpl(
            WorkHourSubmissionRepository submissionRepository,
            BookingRepository bookingRepository,
            WorkerProfileRepository workerProfileRepository,
            EmployerProfileRepository employerProfileRepository,
            WorkHourReviewHistoryRepository reviewHistoryRepository) {

        this.submissionRepository = submissionRepository;
        this.bookingRepository = bookingRepository;
        this.workerProfileRepository = workerProfileRepository;
        this.employerProfileRepository = employerProfileRepository;
        this.reviewHistoryRepository = reviewHistoryRepository;
    }

    private AuthenticatedUser getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new IllegalStateException("Authenticated user not found");
        }

        return user;
    }
    private void saveReviewHistory(
            WorkHourSubmission submission,
            Long employerUserId) {

        EmployerProfile employer = employerProfileRepository
                .findByUserAccount_Id(employerUserId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employer profile not found"));

        WorkHourReviewHistory history = new WorkHourReviewHistory();

        history.setSubmission(submission);
        history.setEmployerProfile(employer);
        history.setSubmittedHours(submission.getSubmittedHours());
        history.setApprovedHours(submission.getApprovedHours());
        history.setStatus(submission.getStatus());
        history.setRemarks(submission.getEmployerRemarks());

        reviewHistoryRepository.save(history);
    }

    @Override
    public WorkHourSubmissionResponse submitHours(
            WorkHourSubmissionRequest request) {

        AuthenticatedUser user = getCurrentUser();

        if (user.role() != Role.WORKER) {
            throw new IllegalStateException(
                    "Only workers can submit working hours");
        }

        WorkerProfile worker = workerProfileRepository
                .findByUserAccount_Id(user.id())
                .orElseThrow(() ->
                        new IllegalArgumentException("Worker profile not found"));

        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Booking not found"));

        if (!booking.getWorkerProfile().getId().equals(worker.getId())) {
            throw new IllegalStateException(
                    "You are not assigned to this booking");
        }
        // Prevent multiple initial submissions for the same booking
        boolean alreadySubmitted =
                submissionRepository
                        .existsByBooking_IdAndOriginalSubmissionIsNull(
                                booking.getId()
                        );

        if (alreadySubmitted) {
            throw new IllegalStateException(
                    "Work hours have already been submitted for this booking. " +
                            "You cannot create another initial submission."
            );
        }

        WorkHourSubmission submission = new WorkHourSubmission();
        submission.setBooking(booking);
        submission.setWorkerProfile(worker);
        submission.setSubmittedHours(request.submittedHours());
        submission.setWorkerRemarks(request.workerRemarks());

        WorkHourSubmission saved =
                submissionRepository.save(submission);

        return toSubmissionResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkHourSubmissionResponse> getMySubmissions() {

        AuthenticatedUser user = getCurrentUser();

        if (user.role() != Role.WORKER) {
            throw new IllegalStateException(
                    "Only workers can view their submissions");
        }

        WorkerProfile worker = workerProfileRepository
                .findByUserAccount_Id(user.id())
                .orElseThrow(() ->
                        new IllegalArgumentException("Worker profile not found"));

        return submissionRepository
                .findByWorkerProfile_Id(worker.getId())
                .stream()
                .map(this::toSubmissionResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkHourSubmissionResponse> getBookingSubmissions(
            Long bookingId) {

        AuthenticatedUser user = getCurrentUser();

        if (user.role() != Role.EMPLOYER) {
            throw new IllegalStateException(
                    "Only employers can view booking submissions");
        }

        Booking booking = bookingRepository
                .findByIdAndEmployerProfile_Id(
                        bookingId,
                        getEmployerProfileId(user.id()))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Booking not found or access denied"));

        return submissionRepository
                .findByBooking_Id(booking.getId())
                .stream()
                .map(this::toSubmissionResponse)
                .toList();
    }

    private Long getEmployerProfileId(Long userId) {

        return employerProfileRepository
                .findByUserAccount_Id(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Employer profile not found"))
                .getId();
    }

    private WorkHourSubmissionResponse toSubmissionResponse(
            WorkHourSubmission submission) {

        return new WorkHourSubmissionResponse(
                submission.getId(),
                submission.getBooking().getId(),
                submission.getWorkerProfile().getId(),
                submission.getSubmittedHours(),
                submission.getApprovedHours(),
                submission.getStatus(),
                submission.getWorkerRemarks(),
                submission.getEmployerRemarks(),
                submission.getSubmittedAt(),
                submission.getReviewedAt()
        );
    }


    @Override
    public WorkHourReviewResponse approveHours(
            Long submissionId,
            WorkHourReviewRequest request) {

        AuthenticatedUser user = getCurrentUser();

        if (user.role() != Role.EMPLOYER) {
            throw new IllegalStateException(
                    "Only employers can approve working hours");
        }

        Long employerId = getEmployerProfileId(user.id());

        WorkHourSubmission submission = submissionRepository
                .findById(submissionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Work hour submission not found"));

        Booking booking = bookingRepository
                .findByIdAndEmployerProfile_Id(
                        submission.getBooking().getId(),
                        employerId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "You are not authorized to approve this submission"));

        if (submission.getStatus() != WorkHourStatus.SUBMITTED
                && submission.getStatus() != WorkHourStatus.UNDER_REVIEW) {
            throw new IllegalStateException(
                    "This submission cannot be approved in its current status");
        }

        if (request.approvedHours().compareTo(
                submission.getSubmittedHours()) > 0) {
            throw new IllegalArgumentException(
                    "Approved hours cannot exceed submitted hours");
        }
        // Prevent multiple approved submissions for the same booking
        boolean alreadyApproved =
                submissionRepository.existsByBooking_IdAndStatus(
                        booking.getId(),
                        WorkHourStatus.APPROVED
                );

        if (alreadyApproved) {
            throw new IllegalStateException(
                    "This booking already has an approved work hours submission");
        }

        submission.setApprovedHours(request.approvedHours());
        submission.setEmployerRemarks(request.employerRemarks());
        submission.setStatus(WorkHourStatus.APPROVED);
        submission.setReviewedAt(java.time.LocalDateTime.now());

        WorkHourSubmission saved =
                submissionRepository.save(submission);
        saveReviewHistory(saved, user.id());

        return new WorkHourReviewResponse(
                saved.getId(),
                booking.getId(),
                saved.getSubmittedHours(),
                saved.getApprovedHours(),
                saved.getStatus(),
                saved.getEmployerRemarks(),
                saved.getReviewedAt()
        );
    }



    @Override
    public WorkHourReviewResponse rejectHours(
            Long submissionId,
            String employerRemarks) {

        AuthenticatedUser user = getCurrentUser();

        if (user.role() != Role.EMPLOYER) {
            throw new IllegalStateException(
                    "Only employers can reject working hours");
        }

        if (employerRemarks == null || employerRemarks.isBlank()) {
            throw new IllegalArgumentException(
                    "Rejection remarks are required");
        }

        Long employerId = getEmployerProfileId(user.id());

        WorkHourSubmission submission = submissionRepository
                .findById(submissionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Work hour submission not found"));

        Booking booking = bookingRepository
                .findByIdAndEmployerProfile_Id(
                        submission.getBooking().getId(),
                        employerId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "You are not authorized to reject this submission"));

        if (submission.getStatus() != WorkHourStatus.SUBMITTED
                && submission.getStatus() != WorkHourStatus.UNDER_REVIEW) {
            throw new IllegalStateException(
                    "This submission cannot be rejected in its current status");
        }

        submission.setStatus(WorkHourStatus.REJECTED);
        submission.setEmployerRemarks(employerRemarks);
        submission.setReviewedAt(java.time.LocalDateTime.now());

        WorkHourSubmission saved =
                submissionRepository.save(submission);
        saveReviewHistory(saved, user.id());

        return new WorkHourReviewResponse(
                saved.getId(),
                booking.getId(),
                saved.getSubmittedHours(),
                saved.getApprovedHours(),
                saved.getStatus(),
                saved.getEmployerRemarks(),
                saved.getReviewedAt()
        );
    }


    @Override
    public WorkHourReviewResponse requestCorrection(
            Long submissionId,
            String employerRemarks) {

        AuthenticatedUser user = getCurrentUser();

        if (user.role() != Role.EMPLOYER) {
            throw new IllegalStateException(
                    "Only employers can request corrections");
        }

        if (employerRemarks == null || employerRemarks.isBlank()) {
            throw new IllegalArgumentException(
                    "Correction remarks are required");
        }

        Long employerId = getEmployerProfileId(user.id());

        WorkHourSubmission submission = submissionRepository
                .findById(submissionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Work hour submission not found"));

        Booking booking = bookingRepository
                .findByIdAndEmployerProfile_Id(
                        submission.getBooking().getId(),
                        employerId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "You are not authorized to request corrections"));

        if (submission.getStatus() != WorkHourStatus.SUBMITTED
                && submission.getStatus() != WorkHourStatus.UNDER_REVIEW) {
            throw new IllegalStateException(
                    "Corrections cannot be requested for this submission");
        }

        submission.setStatus(WorkHourStatus.CORRECTION_REQUESTED);
        submission.setEmployerRemarks(employerRemarks);
        submission.setReviewedAt(java.time.LocalDateTime.now());

        WorkHourSubmission saved =
                submissionRepository.save(submission);
        saveReviewHistory(saved, user.id());

        return new WorkHourReviewResponse(
                saved.getId(),
                booking.getId(),
                saved.getSubmittedHours(),
                saved.getApprovedHours(),
                saved.getStatus(),
                saved.getEmployerRemarks(),
                saved.getReviewedAt()
        );
    }

    @Override
    public WorkHourSubmissionResponse resubmitCorrectedHours(
            Long submissionId,
            WorkHourCorrectionRequest request) {

        AuthenticatedUser user = getCurrentUser();

        if (user.role() != Role.WORKER) {
            throw new IllegalStateException(
                    "Only workers can resubmit corrected hours");
        }

        WorkerProfile worker = workerProfileRepository
                .findByUserAccount_Id(user.id())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Worker profile not found"));

        WorkHourSubmission originalSubmission = submissionRepository
                .findByIdAndWorkerProfile_Id(
                        submissionId,
                        worker.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Submission not found or access denied"));

        if (originalSubmission.getStatus()
                != WorkHourStatus.CORRECTION_REQUESTED) {
            throw new IllegalStateException(
                    "This submission is not awaiting correction");
        }
        // Prevent duplicate corrected submissions
        boolean correctionAlreadySubmitted =
                submissionRepository
                        .existsByOriginalSubmission_Id(
                                originalSubmission.getId()
                        );

        if (correctionAlreadySubmitted) {
            throw new IllegalStateException(
                    "Corrected hours have already been submitted for this request");
        }

        WorkHourSubmission correctedSubmission =
                new WorkHourSubmission();

        correctedSubmission.setBooking(
                originalSubmission.getBooking());

        correctedSubmission.setWorkerProfile(worker);

        correctedSubmission.setSubmittedHours(
                request.correctedHours());

        correctedSubmission.setWorkerRemarks(
                request.workerRemarks());

        correctedSubmission.setOriginalSubmission(
                originalSubmission);

        correctedSubmission.setStatus(
                WorkHourStatus.SUBMITTED);

        WorkHourSubmission saved =
                submissionRepository.save(correctedSubmission);

        return toSubmissionResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkHourReviewHistoryResponse> getReviewHistory(
            Long submissionId) {

        AuthenticatedUser user = getCurrentUser();

        WorkHourSubmission submission;

        if (user.role() == Role.WORKER) {

            WorkerProfile worker = workerProfileRepository
                    .findByUserAccount_Id(user.id())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Worker profile not found"));

            submission = submissionRepository
                    .findByIdAndWorkerProfile_Id(
                            submissionId,
                            worker.getId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Submission not found or access denied"));

        } else if (user.role() == Role.EMPLOYER) {

            Long employerId = getEmployerProfileId(user.id());

            submission = submissionRepository
                    .findById(submissionId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Submission not found"));

            bookingRepository
                    .findByIdAndEmployerProfile_Id(
                            submission.getBooking().getId(),
                            employerId)
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "You are not authorized to view this history"));

        } else {
            throw new IllegalStateException(
                    "You are not authorized to view review history");
        }

        return reviewHistoryRepository
                .findBySubmission_IdOrderByReviewedAtDesc(
                        submission.getId())
                .stream()
                .map(history -> new WorkHourReviewHistoryResponse(
                        history.getId(),
                        history.getSubmission().getId(),
                        history.getEmployerProfile().getId(),
                        history.getSubmittedHours(),
                        history.getApprovedHours(),
                        history.getStatus(),
                        history.getRemarks(),
                        history.getReviewedAt()
                ))
                .toList();
    }



}
