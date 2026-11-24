package com.hourlyhire.workmanagement.booking.controller;

import com.hourlyhire.workmanagement.booking.dto.request.AttendanceCheckInRequest;
import com.hourlyhire.workmanagement.booking.dto.response.AttendanceResponse;
import com.hourlyhire.workmanagement.booking.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Tag(name = "10 Attendance Management",
        description = "APIs for worker check-in, check-out and attendance history"
)
@SecurityRequirement(name = "bearerAuth")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Worker check-in. ACCESS: WORKER",
            description = "Allows an authenticated worker to check in for an accepted booking")
    public ResponseEntity<AttendanceResponse> checkIn(
            @Valid @RequestBody AttendanceCheckInRequest request) {

        AttendanceResponse response = attendanceService.checkIn(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{attendanceId}/check-out")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Worker check-out. ACCESS: WORKER",
            description = "Allows a worker to check out from an active attendance record")
    public ResponseEntity<AttendanceResponse> checkOut(
            @PathVariable Long attendanceId) {

        AttendanceResponse response =
                attendanceService.checkOut(attendanceId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{attendanceId}")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Get attendance by ID. ACCESS: WORKER",
            description = "Retrieves an attendance record belonging to the authenticated worker")
    public ResponseEntity<AttendanceResponse> getAttendanceById(
            @PathVariable Long attendanceId) {

        AttendanceResponse response =
                attendanceService.getAttendanceById(attendanceId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-history")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Get my attendance history. ACCESS: WORKER",
            description = "Retrieves the attendance history of the authenticated worker")
    public ResponseEntity<List<AttendanceResponse>> getMyAttendanceHistory() {

        List<AttendanceResponse> response =
                attendanceService.getMyAttendanceHistory();

        return ResponseEntity.ok(response);
    }
    @GetMapping("/employer/bookings/{bookingId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Get worker attendance by booking. ACCESS:EMPLOYER",
            description = "Allows an employer to view attendance for a booking belonging to them")
    public ResponseEntity<AttendanceResponse> getAttendanceByBookingForEmployer(
            @PathVariable Long bookingId) {

        AttendanceResponse response =
                attendanceService.getAttendanceByBookingForEmployer(bookingId);

        return ResponseEntity.ok(response);
    }
}
