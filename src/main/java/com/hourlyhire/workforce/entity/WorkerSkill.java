
package com.hourlyhire.workforce.entity;

import com.hourlyhire.workforce.enums.ProficiencyLevel;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "worker_skills",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_worker_skills_profile_skill",
                        columnNames = {"worker_profile_id", "skill_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_worker_skills_worker_profile",
                        columnList = "worker_profile_id"
                ),
                @Index(
                        name = "idx_worker_skills_skill",
                        columnList = "skill_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class WorkerSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationship with WorkerProfile
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "worker_profile_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_worker_skills_worker_profile"
            )
    )
    private WorkerProfile workerProfile;

    // Relationship with Skill
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "skill_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_worker_skills_skill"
            )
    )
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(name = "proficiency_level", nullable = false, length = 30)
    private ProficiencyLevel proficiencyLevel;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
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
