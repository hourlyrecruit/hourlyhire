package com.hourlyhire.workforce.entity;

import com.hourlyhire.workforce.enums.ProfileStatus;
import com.hourlyhire.workforce.enums.VerificationStatus;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "worker_profiles",
        uniqueConstraints = {
                @UniqueConstraint( name = "uk_worker_profiles_user_id",
                        columnNames = "user_id" )
        },
        indexes = {
                @Index( name = "idx_worker_profile_status",
                        columnList = "profile_status"),
                @Index( name = "idx_worker_verification_status",
                        columnList = "verification_status" ),
                @Index( name = "idx_worker_discoverable",
                        columnList = "is_discoverable" )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class WorkerProfile {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "user_id", nullable = false, unique = true)
        private Long userId;

        @Column(name = "bio", columnDefinition = "TEXT")
        private String bio;

        @Column(name = "profile_photo_url", length = 500)
        private String profilePhotoUrl;

        @Enumerated(EnumType.STRING)
        @Column(name = "verification_status", nullable = false, length = 30)
        private VerificationStatus verificationStatus =
                VerificationStatus.PENDING;

        @Enumerated(EnumType.STRING)
        @Column(name = "profile_status", nullable = false, length = 30)
        private ProfileStatus profileStatus = ProfileStatus.ACTIVE;

        @Column(name = "is_discoverable", nullable = false)
        private boolean discoverable = false;

        @Column(name = "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @Column(name = "updated_at", nullable = false)
        private LocalDateTime updatedAt;

        @PrePersist
        protected void onCreate() {
            LocalDateTime now = LocalDateTime.now();

            if (createdAt == null) {
                createdAt = now;
            }

            updatedAt = now;
        }

        @PreUpdate
        protected void onUpdate() {
            updatedAt = LocalDateTime.now();
        }
}
