
package com.hourlyhire.workmanagement.hours.repository;

import com.hourlyhire.workmanagement.hours.entity.WorkHourReviewHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkHourReviewHistoryRepository
        extends JpaRepository<WorkHourReviewHistory, Long> {

    // Get all review history for a submission
    List<WorkHourReviewHistory> findBySubmission_IdOrderByReviewedAtDesc(
            Long submissionId
    );

    // Get all review history for an employer
    List<WorkHourReviewHistory> findByEmployerProfile_IdOrderByReviewedAtDesc(
            Long employerId
    );
}
