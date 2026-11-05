package com.hourlyhire.notification.dto;

import com.hourlyhire.notification.entity.Notification;
import com.hourlyhire.notification.event.NotificationEventType;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        NotificationEventType eventType,
        String subject,
        String body,
        boolean read,
        Instant createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(n.getId(), n.getEventType(), n.getSubject(),
                n.getBody(), n.getReadAt() != null, n.getCreatedAt());
    }
}