package com.hourlyhire.workmanagement.booking.mapper;

import com.hourlyhire.workmanagement.booking.dto.response.BookingResponse;
import com.hourlyhire.workmanagement.booking.entity.Booking;

import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingResponse toResponse(Booking booking) {

        return BookingResponse.builder()
                .id(booking.getId())
                .employerProfileId(
                        booking.getEmployerProfile().getId())
                .workerProfileId(
                        booking.getWorkerProfile().getId())
                .jobTitle(booking.getJobTitle())
                .description(booking.getDescription())
                .workDate(booking.getWorkDate())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .location(booking.getLocation())
                .hourlyRate(booking.getHourlyRate())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}
