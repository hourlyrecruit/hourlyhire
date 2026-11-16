
package com.hourlyhire.workforce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "service_areas",
        indexes = {
                @Index(
                        name = "idx_service_area_worker_profile",
                        columnList = "worker_profile_id"
                ),
                @Index(
                        name = "idx_service_area_pincode",
                        columnList = "pincode"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ServiceArea {

    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many Service Areas belong to one Worker Profile
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "worker_profile_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_service_area_worker_profile"
            )
    )
    private WorkerProfile workerProfile;

    // Location details
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "pincode", nullable = false, length = 10)
    private String pincode;

    @Column(name = "country", nullable = false, length = 100)
    private String country = "India";

    // Service area status
    @Column(name = "active", nullable = false)
    private boolean active = true;

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
