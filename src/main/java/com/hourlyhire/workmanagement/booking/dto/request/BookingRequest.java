
package com.hourlyhire.workmanagement.booking.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {

    private Long workerProfileId;

    private String jobTitle;

    private String description;

    private LocalDate workDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String location;

    private BigDecimal hourlyRate;
}
