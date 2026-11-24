package com.hourlyhire.workmanagement.booking.controller;

import com.hourlyhire.workmanagement.booking.dto.response.EarningsResponse;
import com.hourlyhire.workmanagement.booking.dto.response.EarningsSummaryResponse;
import com.hourlyhire.workmanagement.booking.service.EarningsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/earnings")
@Tag(name = "11 Earnings Management", description = "APIs for calculating and viewing worker earnings")
@RequiredArgsConstructor
public class EarningsController {

    private final EarningsService earningsService;

    // Employer: Calculate earnings for a booking
    @Operation(
            summary = "Calculate booking earnings. Access: EMPLOYER",
            description = "Allows an employer to view earnings for a completed booking."
    )
    @GetMapping("/employer/bookings/{bookingId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<EarningsResponse> getBookingEarnings(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                earningsService.calculateBookingEarnings(bookingId)
        );
    }

    // Worker: View earnings for an assigned booking
    @Operation(
            summary = "Get my booking earnings. Access: WORKER",
            description = "Allows a worker to view earnings for their completed booking."
    )
    @GetMapping("/my-bookings/{bookingId}")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<EarningsResponse> getMyBookingEarnings(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                earningsService.getMyBookingEarnings(bookingId)
        );
    }
    @Operation(
            summary = "Get my earnings history. Access: WORKER",
            description = "Allows a worker to view earnings history for all completed bookings."
    )
    @PreAuthorize("hasRole('WORKER')")
    @GetMapping("/my-history")
    public ResponseEntity<List<EarningsResponse>> getMyEarningsHistory() {

        List<EarningsResponse> response =
                earningsService.getMyEarningsHistory();

        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Get employer earnings history. Access: EMPLOYER",
            description = "Allows an employer to view earnings history for all completed bookings."
    )
    @PreAuthorize("hasRole('EMPLOYER')")
    @GetMapping("/employer/history")
    public ResponseEntity<List<EarningsResponse>> getEmployerEarningsHistory() {

        List<EarningsResponse> response =
                earningsService.getEmployerEarningsHistory();

        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Get my earnings summary. Access: WORKER",
            description = "Returns total completed bookings, working hours and gross earnings for the authenticated worker."
    )
    @PreAuthorize("hasRole('WORKER')")
    @GetMapping("/my-summary")
    public ResponseEntity<EarningsSummaryResponse> getMyEarningsSummary() {

        EarningsSummaryResponse response =
                earningsService.getMyEarningsSummary();

        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Get employer earnings summary. Access: EMPLOYER",
            description = "Returns total completed bookings, working hours and gross earnings for the authenticated employer."
    )
    @PreAuthorize("hasRole('EMPLOYER')")
    @GetMapping("/employer/summary")
    public ResponseEntity<EarningsSummaryResponse> getEmployerEarningsSummary() {

        EarningsSummaryResponse response =
                earningsService.getEmployerEarningsSummary();

        return ResponseEntity.ok(response);
    }
}
