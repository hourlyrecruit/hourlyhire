package com.hourlyhire.workmanagement.booking.mapper;
import com.hourlyhire.workmanagement.booking.dto.response.AttendanceResponse;
import com.hourlyhire.workmanagement.booking.entity.Attendance;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {

    public AttendanceResponse toResponse(Attendance attendance) {

        return AttendanceResponse.builder()
                .id(attendance.getId())
                .bookingId(attendance.getBooking().getId())
                .workerProfileId(attendance.getWorkerProfile().getId())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .totalHours(attendance.getTotalHours())
                .status(attendance.getStatus())
                .remarks(attendance.getRemarks())
                .createdAt(attendance.getCreatedAt())
                .updatedAt(attendance.getUpdatedAt())
                .build();
    }
}
