
package com.hourlyhire.workforce.entity;

import com.hourlyhire.workforce.enums.AvailabilityType;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "worker_availabilities",
        indexes = {
                @Index(
                        name = "idx_availability_worker",
                        columnList = "worker_profile_id"
                ),
                @Index(
                        name = "idx_availability_date",
                        columnList = "available_date"
                ),
                @Index(
                        name = "idx_availability_day",
                        columnList = "day_of_week"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class WorkerAvailability {

    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Worker Profile relationship
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "worker_profile_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_availability_worker_profile"
            )
    )
    private WorkerProfile workerProfile;

    // Weekly or date-specific availability
    @Enumerated(EnumType.STRING)
    @Column(
            name = "availability_type",
            nullable = false,
            length = 30
    )
    private AvailabilityType availabilityType;

    // Weekly availability day
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", length = 15)
    private DayOfWeek dayOfWeek;

    // Date-specific availability
    @Column(name = "available_date")
    private LocalDate availableDate;

    // Time slot
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // Availability status
    @Column(name = "available", nullable = false)
    private boolean available = true;

    // Audit timestamps
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }
}
