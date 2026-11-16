
package com.hourlyhire.workforce.dto.response;

import com.hourlyhire.workforce.enums.AvailabilityType;

import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class AvailabilityResponse {

    private Long id;

    private Long workerProfileId;

    private AvailabilityType availabilityType;

    private DayOfWeek dayOfWeek;

    private LocalDate availableDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private boolean available;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
