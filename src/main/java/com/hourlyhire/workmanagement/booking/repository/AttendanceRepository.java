package com.hourlyhire.workmanagement.booking.repository;

import com.hourlyhire.workmanagement.booking.entity.Attendance;
import com.hourlyhire.workmanagement.booking.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Find attendance using booking ID
    Optional<Attendance> findByBooking_Id(Long bookingId);

    // Check whether attendance already exists for a booking
    boolean existsByBooking_Id(Long bookingId);

    // Get all attendance records of a worker
    List<Attendance> findAllByWorkerProfile_Id(Long workerProfileId);

    // Get attendance records by worker and status
    List<Attendance> findAllByWorkerProfile_IdAndStatus(
            Long workerProfileId,
            AttendanceStatus status
    );
}
