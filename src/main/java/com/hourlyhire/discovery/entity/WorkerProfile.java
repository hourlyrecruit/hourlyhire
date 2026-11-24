
package com.hourlyhire.discovery.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "worker_profiles", schema = "public")
public class WorkerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "is_discoverable", nullable = false)
    private boolean discoverable;

    @Column(name = "profile_status", nullable = false)
    private String profileStatus;

    @Column(name = "verification_status", nullable = false)
    private String verificationStatus;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    @Column(name = "bio", columnDefinition = "text")
    private String bio;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public WorkerProfile() {
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isDiscoverable() {
        return discoverable;
    }

    public String getProfileStatus() {
        return profileStatus;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public String getBio() {
        return bio;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
