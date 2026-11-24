package com.hourlyhire.workmanagement.booking.dto.response;

import com.hourlyhire.workmanagement.booking.enums.AttendanceStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class AttendanceResponse {

    private Long id;

    private Long bookingId;

    private Long workerProfileId;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private BigDecimal totalHours;

    private AttendanceStatus status;

    private String remarks;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

