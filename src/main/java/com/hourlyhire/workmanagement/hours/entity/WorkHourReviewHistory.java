
package com.hourlyhire.workmanagement.hours.entity;

import com.hourlyhire.workforce.entity.EmployerProfile;
import com.hourlyhire.workmanagement.hours.enums.WorkHourStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_hour_review_history")
@Getter
@Setter
@NoArgsConstructor
public class WorkHourReviewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private WorkHourSubmission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private EmployerProfile employerProfile;

    @Column(name = "submitted_hours", precision = 8, scale = 2)
    private BigDecimal submittedHours;

    @Column(name = "approved_hours", precision = 8, scale = 2)
    private BigDecimal approvedHours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkHourStatus status;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "reviewed_at", nullable = false)
    private LocalDateTime reviewedAt;

    @PrePersist
    protected void onCreate() {
        if (reviewedAt == null) {
            reviewedAt = LocalDateTime.now();
        }
    }
}
