
package com.hourlyhire.workforce.dto.request;

import com.hourlyhire.workforce.enums.AvailabilityType;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class CreateAvailabilityRequest {

    @NotNull(message = "Availability type is required")
    private AvailabilityType availabilityType;

    // Required for WEEKLY availability
    private DayOfWeek dayOfWeek;

    // Required for DATE_SPECIFIC availability
    private LocalDate availableDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    private Boolean available = true;
}
