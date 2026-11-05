package com.hourlyhire.notification.event;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record NotificationEvent(
        UUID eventId,
        NotificationEventType eventType,
        int version,
        Instant occurredAt,
        Long userId,
        Map<String, String> data
) {
    public static NotificationEvent of(NotificationEventType type, Long userId, Map<String, String> data) {
        return new NotificationEvent(UUID.randomUUID(), type, 1, Instant.now(), userId, data);
    }
}