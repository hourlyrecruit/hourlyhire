
package com.hourlyhire.workmanagement.booking.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class EarningsResponse {

    private Long bookingId;

    private Long workerProfileId;

    private String jobTitle;

    private BigDecimal totalHours;

    private BigDecimal hourlyRate;

    private BigDecimal grossEarnings;

    private String paymentStatus;
}
