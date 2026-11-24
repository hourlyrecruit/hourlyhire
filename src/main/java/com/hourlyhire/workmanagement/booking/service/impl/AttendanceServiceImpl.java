package com.hourlyhire.workmanagement.booking.service.impl;

import com.hourlyhire.authentication.enums.Role;
import com.hourlyhire.security.jwt.AuthenticatedUser;

import com.hourlyhire.workforce.entity.EmployerProfile;
import com.hourlyhire.workforce.entity.WorkerProfile;
import com.hourlyhire.workforce.repository.EmployerProfileRepository;
import com.hourlyhire.workforce.repository.WorkerProfileRepository;

import com.hourlyhire.workmanagement.booking.dto.request.AttendanceCheckInRequest;
import com.hourlyhire.workmanagement.booking.dto.response.AttendanceResponse;
import com.hourlyhire.workmanagement.booking.entity.Attendance;
import com.hourlyhire.workmanagement.booking.entity.Booking;
import com.hourlyhire.workmanagement.booking.enums.AttendanceStatus;
import com.hourlyhire.workmanagement.booking.enums.BookingStatus;
import com.hourlyhire.workmanagement.booking.mapper.AttendanceMapper;
import com.hourlyhire.workmanagement.booking.repository.AttendanceRepository;
import com.hourlyhire.workmanagement.booking.repository.BookingRepository;

import com.hourlyhire.workmanagement.booking.service.AttendanceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceServiceImpl implements AttendanceService {
    private final EmployerProfileRepository employerProfileRepository;
    private final AttendanceRepository attendanceRepository;
    private final BookingRepository bookingRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final AttendanceMapper attendanceMapper;

    // Get the authenticated worker
    private WorkerProfile getAuthenticatedWorker() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new AccessDeniedException("User is not authenticated");
        }

        if (user.role() != Role.WORKER) {
            throw new AccessDeniedException(
                    "Only workers can manage their attendance"
            );
        }

        return workerProfileRepository.findByUserAccount_Id(user.id())
                .orElseThrow(() ->
                        new IllegalArgumentException("Worker profile not found")
                );
    }


    // 1. Worker check-in
    @Override
    @Transactional
    public AttendanceResponse checkIn(AttendanceCheckInRequest request) {

        WorkerProfile worker = getAuthenticatedWorker();

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Booking not found")
                );

        // Verify booking belongs to the authenticated worker
        if (!booking.getWorkerProfile().getId().equals(worker.getId())) {
            throw new AccessDeniedException(
                    "You are not assigned to this booking"
            );
        }

        // Only accepted bookings can be checked in
        if (booking.getStatus() != BookingStatus.ACCEPTED) {
            throw new IllegalStateException(
                    "Only accepted bookings allow check-in"
            );
        }

        // Prevent duplicate attendance
        if (attendanceRepository.existsByBooking_Id(booking.getId())) {
            throw new IllegalStateException(
                    "Attendance already exists for this booking"
            );
        }

        Attendance attendance = new Attendance();

        attendance.setBooking(booking);
        attendance.setWorkerProfile(worker);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setStatus(AttendanceStatus.CHECKED_IN);
        attendance.setRemarks(request.getRemarks());

        // Save attendance
        Attendance savedAttendance =
                attendanceRepository.save(attendance);

        // Update booking status
        booking.setStatus(BookingStatus.IN_PROGRESS);
        bookingRepository.save(booking);

        return attendanceMapper.toResponse(savedAttendance);
    }


    // 2. Worker check-out
    @Override
    @Transactional
    public AttendanceResponse checkOut(Long attendanceId) {

        // Get authenticated worker
        WorkerProfile worker = getAuthenticatedWorker();

        // Find attendance
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Attendance not found")
                );

        // Verify attendance belongs to authenticated worker
        if (!attendance.getWorkerProfile().getId().equals(worker.getId())) {
            throw new AccessDeniedException(
                    "You are not authorized to check out this attendance"
            );
        }

        // Prevent duplicate checkout
        if (attendance.getStatus() != AttendanceStatus.CHECKED_IN) {
            throw new IllegalStateException(
                    "Only checked-in attendance can be checked out"
            );
        }

        // Record checkout time
        LocalDateTime checkOutTime = LocalDateTime.now();
        attendance.setCheckOutTime(checkOutTime);

        // Calculate total working hours
        long minutes = Duration.between(
                attendance.getCheckInTime(),
                checkOutTime
        ).toMinutes();

        BigDecimal totalHours = BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        attendance.setTotalHours(totalHours);

        // Update attendance status
        attendance.setStatus(AttendanceStatus.CHECKED_OUT);

        // Save attendance
        Attendance savedAttendance =
                attendanceRepository.save(attendance);

        // Update booking status
        Booking booking = attendance.getBooking();
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        return attendanceMapper.toResponse(savedAttendance);
    }


    // 3. Get attendance by ID
    @Override
    @Transactional(readOnly = true)
    public AttendanceResponse getAttendanceById(Long attendanceId) {

        WorkerProfile worker = getAuthenticatedWorker();

        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Attendance not found")
                );

        if (!attendance.getWorkerProfile().getId().equals(worker.getId())) {
            throw new AccessDeniedException(
                    "You cannot access another worker's attendance"
            );
        }

        return attendanceMapper.toResponse(attendance);
    }

    // 4. Get my attendance history
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getMyAttendanceHistory() {

        WorkerProfile worker = getAuthenticatedWorker();

        return attendanceRepository
                .findAllByWorkerProfile_Id(worker.getId())
                .stream()
                .map(attendanceMapper::toResponse)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public AttendanceResponse getAttendanceByBookingForEmployer(Long bookingId) {

        // Get authenticated user
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof AuthenticatedUser user)) {
            throw new AccessDeniedException("Invalid authenticated user");
        }

        // Verify employer role
        if (user.role() != Role.EMPLOYER) {
            throw new AccessDeniedException("Only employers can view attendance");
        }

        // Find employer profile
        EmployerProfile employer = employerProfileRepository
                .findByUserAccount_Id(user.id())
                .orElseThrow(() ->
                        new EntityNotFoundException("Employer profile not found"));

        // Find booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Booking not found"));

        // Verify booking ownership
        if (!booking.getEmployerProfile().getId().equals(employer.getId())) {
            throw new AccessDeniedException(
                    "You are not authorized to view this booking's attendance");
        }

        // Find attendance
        Attendance attendance = attendanceRepository.findByBooking_Id(bookingId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Attendance not found for this booking"));

        return attendanceMapper.toResponse(attendance);
    }
}

