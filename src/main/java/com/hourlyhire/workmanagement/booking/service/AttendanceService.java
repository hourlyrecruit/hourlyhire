package com.hourlyhire.workmanagement.booking.service;

import com.hourlyhire.workmanagement.booking.dto.request.AttendanceCheckInRequest;
import com.hourlyhire.workmanagement.booking.dto.response.AttendanceResponse;

import java.util.List;

public interface AttendanceService {

    // Worker checks in for an accepted booking
    AttendanceResponse checkIn(AttendanceCheckInRequest request);

    // Worker checks out from an active attendance
    AttendanceResponse checkOut(Long attendanceId);

    // Get attendance by ID
    AttendanceResponse getAttendanceById(Long attendanceId);

    // Get attendance history of the logged-in worker
    List<AttendanceResponse> getMyAttendanceHistory();

    AttendanceResponse getAttendanceByBookingForEmployer(Long bookingId);
}
