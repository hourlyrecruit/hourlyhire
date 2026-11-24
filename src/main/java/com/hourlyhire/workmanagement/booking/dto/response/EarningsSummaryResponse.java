package com.hourlyhire.workmanagement.booking.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class EarningsSummaryResponse {

    private Long totalCompletedBookings;

    private BigDecimal totalWorkingHours;

    private BigDecimal totalEarnings;

    private String paymentStatus;
}