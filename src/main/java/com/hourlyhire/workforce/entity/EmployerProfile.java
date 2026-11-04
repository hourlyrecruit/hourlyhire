
package com.hourlyhire.workforce.entity;

import com.hourlyhire.authentication.entity.UserAccount;
import com.hourlyhire.workforce.enums.EmployerType;
import com.hourlyhire.workforce.enums.ProfileStatus;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "employer_profiles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_employer_profiles_user_id",
                        columnNames = "user_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_employer_profile_type",
                        columnList = "employer_type"
                ),
                @Index(
                        name = "idx_employer_profile_status",
                        columnList = "profile_status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class EmployerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One employer profile belongs to one UserAccount
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(
                    name = "fk_employer_profiles_user"
            )
    )
    private UserAccount userAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "employer_type", nullable = false, length = 30)
    private EmployerType employerType;

    @Column(name = "display_name", nullable = false, length = 150)
    private String displayName;

    @Column(name = "company_legal_name", length = 200)
    private String companyLegalName;

    @Column(name = "business_description", columnDefinition = "TEXT")
    private String businessDescription;

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "business_email", length = 150)
    private String businessEmail;

    @Column(name = "website_url", length = 255)
    private String websiteUrl;

    @Column(name = "business_logo_url", length = 500)
    private String businessLogoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_status", nullable = false, length = 30)
    private ProfileStatus profileStatus = ProfileStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
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
