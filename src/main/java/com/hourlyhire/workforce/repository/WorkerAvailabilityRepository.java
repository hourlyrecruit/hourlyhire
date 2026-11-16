
package com.hourlyhire.workforce.repository;

import com.hourlyhire.workforce.entity.WorkerAvailability;
import com.hourlyhire.workforce.enums.AvailabilityType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerAvailabilityRepository
        extends JpaRepository<WorkerAvailability, Long> {

    // Get all availability slots of a worker
    List<WorkerAvailability> findByWorkerProfile_Id(
            Long workerProfileId
    );

    // Get weekly availability slots
    List<WorkerAvailability> findByWorkerProfile_IdAndAvailabilityTypeAndDayOfWeek(
            Long workerProfileId,
            AvailabilityType availabilityType,
            DayOfWeek dayOfWeek
    );

    // Get date-specific availability slots
    List<WorkerAvailability> findByWorkerProfile_IdAndAvailabilityTypeAndAvailableDate(
            Long workerProfileId,
            AvailabilityType availabilityType,
            LocalDate availableDate
    );

    // Get active availability slots
    List<WorkerAvailability> findByWorkerProfile_IdAndAvailableTrue(
            Long workerProfileId
    );

    // Find a particular availability record belonging to a worker
    Optional<WorkerAvailability> findByIdAndWorkerProfile_Id(
            Long id,
            Long workerProfileId
    );

    // Find overlapping time slots for the same worker and day
    List<WorkerAvailability>
    findByWorkerProfile_IdAndAvailabilityTypeAndDayOfWeekAndAvailableTrueAndStartTimeLessThanAndEndTimeGreaterThan(
            Long workerProfileId,
            AvailabilityType availabilityType,
            DayOfWeek dayOfWeek,
            LocalTime newEndTime,
            LocalTime newStartTime
    );

    // Find overlapping time slots for the same worker and date
    List<WorkerAvailability>
    findByWorkerProfile_IdAndAvailabilityTypeAndAvailableDateAndAvailableTrueAndStartTimeLessThanAndEndTimeGreaterThan(
            Long workerProfileId,
            AvailabilityType availabilityType,
            LocalDate availableDate,
            LocalTime newEndTime,
            LocalTime newStartTime
    );
}
