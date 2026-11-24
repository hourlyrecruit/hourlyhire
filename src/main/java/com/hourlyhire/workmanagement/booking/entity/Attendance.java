package com.hourlyhire.workmanagement.booking.entity;
import com.hourlyhire.workforce.entity.WorkerProfile;

import com.hourlyhire.workmanagement.booking.enums.AttendanceStatus;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendances",
        indexes = {
                @Index(name = "idx_attendance_worker",
                        columnList = "worker_profile_id"),
                @Index(name = "idx_attendance_status",
                        columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Associated booking
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "booking_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_attendance_booking")
    )
    private Booking booking;

    // Associated worker
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "worker_profile_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_attendance_worker")
    )
    private WorkerProfile workerProfile;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "total_hours", precision = 5, scale = 2)
    private BigDecimal totalHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AttendanceStatus status = AttendanceStatus.NOT_STARTED;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = AttendanceStatus.NOT_STARTED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
