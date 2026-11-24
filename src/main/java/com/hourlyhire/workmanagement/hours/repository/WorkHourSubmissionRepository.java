package com.hourlyhire.workmanagement.hours.repository;

import com.hourlyhire.workmanagement.hours.entity.WorkHourSubmission;
import com.hourlyhire.workmanagement.hours.enums.WorkHourStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkHourSubmissionRepository
        extends JpaRepository<WorkHourSubmission, Long> {

    // Find all submissions for a particular booking
    List<WorkHourSubmission> findByBooking_Id(Long bookingId);

    // Find submissions belonging to a particular worker
    List<WorkHourSubmission> findByWorkerProfile_Id(Long workerId);

    // Find a submission by its ID and worker ID
    Optional<WorkHourSubmission> findByIdAndWorkerProfile_Id(
            Long id, Long workerId);

    // Find submissions by their current status
    List<WorkHourSubmission> findByStatus(
            com.hourlyhire.workmanagement.hours.enums.WorkHourStatus status);
    Optional<WorkHourSubmission>
    findFirstByBooking_IdAndStatusOrderBySubmittedAtDesc(
            Long bookingId,
            WorkHourStatus status
    );
    boolean existsByBooking_IdAndOriginalSubmissionIsNull(
            Long bookingId
    );

    boolean existsByOriginalSubmission_Id(
            Long originalSubmissionId
    );
    boolean existsByBooking_IdAndStatus(
            Long bookingId,
            WorkHourStatus status
    );
}
