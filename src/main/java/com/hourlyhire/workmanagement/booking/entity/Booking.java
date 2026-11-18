package com.hourlyhire.workmanagement.booking.entity;

import com.hourlyhire.workforce.entity.EmployerProfile;
import com.hourlyhire.workforce.entity.WorkerProfile;


import com.hourlyhire.workmanagement.booking.enums.BookingStatus;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "bookings",
        indexes = {
                @Index(
                        name = "idx_booking_employer",
                        columnList = "employer_profile_id"
                ),
                @Index(
                        name = "idx_booking_worker",
                        columnList = "worker_profile_id"
                ),
                @Index(
                        name = "idx_booking_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Employer relationship
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "employer_profile_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_booking_employer_profile"
            )
    )
    private EmployerProfile employerProfile;

    // Worker relationship
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "worker_profile_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_booking_worker_profile"
            )
    )
    private WorkerProfile workerProfile;

    @Column(name = "job_title", nullable = false, length = 150)
    private String jobTitle;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "location", nullable = false, length = 255)
    private String location;

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private BookingStatus status = BookingStatus.PENDING;

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
            status = BookingStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
