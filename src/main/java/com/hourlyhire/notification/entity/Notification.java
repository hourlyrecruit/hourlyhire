package com.hourlyhire.notification.entity;

import com.hourlyhire.notification.enums.NotificationChannel;
import com.hourlyhire.notification.enums.NotificationStatus;
import com.hourlyhire.notification.event.NotificationEventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "notifications",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_notifications_event_channel",
                columnNames = {"event_id", "channel"}),
        indexes = @Index(name = "idx_notifications_user_created", columnList = "user_id, created_at")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, updatable = false)
    private UUID eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, updatable = false, length = 50)
    private NotificationEventType eventType;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, updatable = false, length = 20)
    private NotificationChannel channel;

    @Column(name = "recipient", length = 254)
    private String recipient;

    @Column(name = "subject", length = 200)
    private String subject;

    @Column(name = "body", length = 2000)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "attempts", nullable = false)
    private int attempts = 0;

    @Column(name = "last_error", length = 500)
    private String lastError;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Notification(UUID eventId, NotificationEventType eventType, Long userId,
                        NotificationChannel channel, String recipient, String subject, String body) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.userId = userId;
        this.channel = channel;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void markSent(Instant now) {
        this.status = NotificationStatus.SENT;
        this.sentAt = now;
        this.lastError = null;
        this.attempts++;
    }

    public void markFailed(String error) {
        this.status = NotificationStatus.FAILED;
        this.attempts++;
        this.lastError = error == null ? null : error.substring(0, Math.min(error.length(), 500));
    }

    public void markRead(Instant now) {
        if (this.readAt == null) {
            this.readAt = now;
        }
    }
}