package com.hourlyhire.workmanagement.booking.dto.response;

import com.hourlyhire.workmanagement.booking.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookingResponse {

    private Long id;

    private Long employerProfileId;

    private Long workerProfileId;

    private String jobTitle;

    private String description;

    private LocalDate workDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String location;

    private BigDecimal hourlyRate;

    private BookingStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
