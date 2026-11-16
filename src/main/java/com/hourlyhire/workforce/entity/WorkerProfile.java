package com.hourlyhire.workforce.entity;

import com.hourlyhire.authentication.entity.UserAccount;
import com.hourlyhire.workforce.enums.ProfileStatus;
import com.hourlyhire.workforce.enums.VerificationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "worker_profiles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_worker_profiles_user_id",
                        columnNames = "user_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_worker_profile_status",
                        columnList = "profile_status"
                ),
                @Index(
                        name = "idx_worker_verification_status",
                        columnList = "verification_status"
                ),
                @Index(
                        name = "idx_worker_discoverable",
                        columnList = "is_discoverable"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class WorkerProfile {

        // Primary Key
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // One WorkerProfile belongs to one UserAccount
        @OneToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(
                name = "user_id",
                referencedColumnName = "id",
                nullable = false,
                unique = true,
                foreignKey = @ForeignKey(
                        name = "fk_worker_profiles_user"
                )
        )
        private UserAccount userAccount;

        // Worker profile information
        @Column(name = "bio", columnDefinition = "TEXT")
        private String bio;

        @Column(name = "profile_photo_url", length = 500)
        private String profilePhotoUrl;

        // Verification status
        @Enumerated(EnumType.STRING)
        @Column(
                name = "verification_status",
                nullable = false,
                length = 30
        )
        private VerificationStatus verificationStatus =
                VerificationStatus.PENDING;

        // Profile status
        @Enumerated(EnumType.STRING)
        @Column(
                name = "profile_status",
                nullable = false,
                length = 30
        )
        private ProfileStatus profileStatus =
                ProfileStatus.ACTIVE;

        // Worker visibility
        @Column(name = "is_discoverable", nullable = false)
        private boolean discoverable = false;

        // Audit timestamps
        @Column(
                name = "created_at",
                nullable = false,
                updatable = false
        )
        private LocalDateTime createdAt;

        @Column(name = "updated_at", nullable = false)
        private LocalDateTime updatedAt;

        // Automatically execute before inserting a record
        @PrePersist
        protected void onCreate() {

                LocalDateTime now = LocalDateTime.now();

                if (createdAt == null) {
                        createdAt = now;
                }

                updatedAt = now;
        }

        // Automatically execute before updating a record
        @PreUpdate
        protected void onUpdate() {

                updatedAt = LocalDateTime.now();
        }
}
