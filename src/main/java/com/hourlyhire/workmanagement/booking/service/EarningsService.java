package com.hourlyhire.workmanagement.booking.service;

import com.hourlyhire.workmanagement.booking.dto.response.EarningsResponse;
import com.hourlyhire.workmanagement.booking.dto.response.EarningsSummaryResponse;

import java.util.List;

public interface EarningsService {

    // Employer: Calculate earnings for a particular booking
    EarningsResponse calculateBookingEarnings(Long bookingId);

    // Worker: View earnings for a particular booking
    EarningsResponse getMyBookingEarnings(Long bookingId);

    // Worker: View earnings history
    List<EarningsResponse> getMyEarningsHistory();

    // Employer: View earnings history
    List<EarningsResponse> getEmployerEarningsHistory();
    // Worker: Get my earnings summary
    EarningsSummaryResponse getMyEarningsSummary();

    // Employer: Get employer earnings summary
    EarningsSummaryResponse getEmployerEarningsSummary();
}