
package com.hourlyhire.workmanagement.hours.entity;

import com.hourlyhire.workmanagement.booking.entity.Booking;
import com.hourlyhire.workmanagement.hours.enums.WorkHourStatus;
import com.hourlyhire.workforce.entity.WorkerProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_hour_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkHourSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private WorkerProfile workerProfile;

    @Column(name = "submitted_hours", nullable = false, precision = 8, scale = 2)
    private BigDecimal submittedHours;

    @Column(name = "approved_hours", precision = 8, scale = 2)
    private BigDecimal approvedHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private WorkHourStatus status = WorkHourStatus.SUBMITTED;

    @Column(name = "worker_remarks", columnDefinition = "TEXT")
    private String workerRemarks;

    @Column(name = "employer_remarks", columnDefinition = "TEXT")
    private String employerRemarks;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_submission_id")
    private WorkHourSubmission originalSubmission;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
