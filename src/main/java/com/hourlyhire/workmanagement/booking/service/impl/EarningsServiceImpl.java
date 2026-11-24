package com.hourlyhire.workmanagement.booking.service.impl;

import com.hourlyhire.security.jwt.AuthenticatedUser;
import com.hourlyhire.workforce.entity.EmployerProfile;
import com.hourlyhire.workforce.entity.WorkerProfile;
import com.hourlyhire.workforce.repository.EmployerProfileRepository;
import com.hourlyhire.workforce.repository.WorkerProfileRepository;

import com.hourlyhire.authentication.enums.Role;


import com.hourlyhire.workmanagement.booking.dto.response.EarningsResponse;
import com.hourlyhire.workmanagement.booking.dto.response.EarningsSummaryResponse;
import com.hourlyhire.workmanagement.booking.entity.Attendance;
import com.hourlyhire.workmanagement.booking.entity.Booking;
import com.hourlyhire.workmanagement.booking.enums.AttendanceStatus;
import com.hourlyhire.workmanagement.booking.enums.BookingStatus;
import com.hourlyhire.workmanagement.booking.repository.AttendanceRepository;
import com.hourlyhire.workmanagement.booking.repository.BookingRepository;
import com.hourlyhire.workmanagement.booking.service.EarningsService;

import com.hourlyhire.workmanagement.hours.entity.WorkHourSubmission;
import com.hourlyhire.workmanagement.hours.enums.WorkHourStatus;
import com.hourlyhire.workmanagement.hours.repository.WorkHourSubmissionRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EarningsServiceImpl implements EarningsService {

    private final BookingRepository bookingRepository;
    private final AttendanceRepository attendanceRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final WorkHourSubmissionRepository workHourSubmissionRepository;

    public EarningsServiceImpl(
            BookingRepository bookingRepository,
            AttendanceRepository attendanceRepository,
            EmployerProfileRepository employerProfileRepository,
            WorkerProfileRepository workerProfileRepository,
            WorkHourSubmissionRepository workHourSubmissionRepository) {

        this.bookingRepository = bookingRepository;
        this.attendanceRepository = attendanceRepository;
        this.employerProfileRepository = employerProfileRepository;
        this.workerProfileRepository = workerProfileRepository;
        this.workHourSubmissionRepository = workHourSubmissionRepository;
    }

    private EmployerProfile getAuthenticatedEmployer() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof AuthenticatedUser user) ||
                user.role() != Role.EMPLOYER) {

            throw new AccessDeniedException("Employer access required");
        }

        return employerProfileRepository.findByUserAccount_Id(user.id())
                .orElseThrow(() ->
                        new IllegalArgumentException("Employer profile not found"));
    }

    private WorkerProfile getAuthenticatedWorker() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof AuthenticatedUser user) ||
                user.role() != Role.WORKER) {

            throw new AccessDeniedException("Worker access required");
        }

        return workerProfileRepository.findByUserAccount_Id(user.id())
                .orElseThrow(() ->
                        new IllegalArgumentException("Worker profile not found"));
    }

    @Override
    public EarningsResponse calculateBookingEarnings(Long bookingId) {

        EmployerProfile employer = getAuthenticatedEmployer();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Booking not found"));

        if (!booking.getEmployerProfile().getId()
                .equals(employer.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to view these earnings");
        }

        return calculateEarnings(booking);
    }

    @Override
    public EarningsResponse getMyBookingEarnings(Long bookingId) {

        WorkerProfile worker = getAuthenticatedWorker();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Booking not found"));

        if (!booking.getWorkerProfile().getId()
                .equals(worker.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to view these earnings");
        }

        return calculateEarnings(booking);
    }

    private EarningsResponse calculateEarnings(Booking booking) {

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Earnings can only be calculated for completed bookings");
        }

        Attendance attendance = attendanceRepository
                .findByBooking_Id(booking.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Attendance not found"));

        if (attendance.getStatus() != AttendanceStatus.CHECKED_OUT) {
            throw new IllegalStateException(
                    "Attendance must be checked out before calculating earnings");
        }

        if (booking.getHourlyRate() == null) {
            throw new IllegalStateException("Hourly rate is required");
        }

        WorkHourSubmission approvedSubmission =
                workHourSubmissionRepository
                        .findFirstByBooking_IdAndStatusOrderBySubmittedAtDesc(
                                booking.getId(),
                                WorkHourStatus.APPROVED
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Approved work hours not found for this booking"));

        BigDecimal approvedHours = approvedSubmission.getApprovedHours();

        if (approvedHours == null ||
                approvedHours.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException(
                    "Valid approved work hours are required");
        }

        BigDecimal grossEarnings = approvedHours
                .multiply(booking.getHourlyRate())
                .setScale(2, RoundingMode.HALF_UP);

        return EarningsResponse.builder()
                .bookingId(booking.getId())
                .workerProfileId(booking.getWorkerProfile().getId())
                .jobTitle(booking.getJobTitle())
                .totalHours(approvedHours)
                .hourlyRate(booking.getHourlyRate())
                .grossEarnings(grossEarnings)
                .paymentStatus("PENDING")
                .build();
    }
    @Override
    public List<EarningsResponse> getMyEarningsHistory() {

        WorkerProfile worker = getAuthenticatedWorker();

        List<Booking> bookings =
                bookingRepository.findAllByWorkerProfile_Id(worker.getId());

        return getEligibleEarningsHistory(bookings);
    }
    @Override
    public List<EarningsResponse> getEmployerEarningsHistory() {

        EmployerProfile employer = getAuthenticatedEmployer();

        List<Booking> bookings =
                bookingRepository.findAllByEmployerProfile_Id(employer.getId());

        return getEligibleEarningsHistory(bookings);
    }
    private List<EarningsResponse> getEligibleEarningsHistory(
            List<Booking> bookings) {

        List<EarningsResponse> earningsHistory = new ArrayList<>();

        for (Booking booking : bookings) {

            if (booking.getStatus() != BookingStatus.COMPLETED) {
                continue;
            }

            Attendance attendance = attendanceRepository
                    .findByBooking_Id(booking.getId())
                    .orElse(null);

            if (attendance == null ||
                    attendance.getStatus() != AttendanceStatus.CHECKED_OUT ||
                    booking.getHourlyRate() == null) {
                continue;
            }

            WorkHourSubmission approvedSubmission =
                    workHourSubmissionRepository
                            .findFirstByBooking_IdAndStatusOrderBySubmittedAtDesc(
                                    booking.getId(),
                                    WorkHourStatus.APPROVED
                            )
                            .orElse(null);

            if (approvedSubmission == null ||
                    approvedSubmission.getApprovedHours() == null ||
                    approvedSubmission.getApprovedHours()
                            .compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            earningsHistory.add(calculateEarnings(booking));
        }

        return earningsHistory;
    }
    @Override
    public EarningsSummaryResponse getMyEarningsSummary() {

        List<EarningsResponse> earningsHistory = getMyEarningsHistory();

        return calculateSummary(earningsHistory);
    }
    @Override
    public EarningsSummaryResponse getEmployerEarningsSummary() {

        List<EarningsResponse> earningsHistory =
                getEmployerEarningsHistory();

        return calculateSummary(earningsHistory);
    }
    private EarningsSummaryResponse calculateSummary(
            List<EarningsResponse> earningsHistory) {

        BigDecimal totalWorkingHours = BigDecimal.ZERO;
        BigDecimal totalEarnings = BigDecimal.ZERO;

        for (EarningsResponse earnings : earningsHistory) {

            totalWorkingHours = totalWorkingHours.add(
                    earnings.getTotalHours()
            );

            totalEarnings = totalEarnings.add(
                    earnings.getGrossEarnings()
            );
        }

        return EarningsSummaryResponse.builder()
                .totalCompletedBookings((long) earningsHistory.size())
                .totalWorkingHours(
                        totalWorkingHours.setScale(2, RoundingMode.HALF_UP)
                )
                .totalEarnings(
                        totalEarnings.setScale(2, RoundingMode.HALF_UP)
                )
                .paymentStatus("PENDING")
                .build();
    }
}
