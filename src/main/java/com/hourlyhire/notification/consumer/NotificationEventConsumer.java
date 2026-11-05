package com.hourlyhire.notification.consumer;

import com.hourlyhire.notification.event.NotificationEvent;
import com.hourlyhire.notification.exception.NonRetryableEventException;
import com.hourlyhire.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @KafkaListener(topics = "${app.kafka.topics.notification-events}")
    public void consume(String payload) {
        NotificationEvent event;
        try {
            event = objectMapper.readValue(payload, NotificationEvent.class);
        } catch (JacksonException e) {
            throw new NonRetryableEventException("Malformed notification event", e);
        }

        if (event.eventId() == null || event.eventType() == null || event.userId() == null) {
            throw new NonRetryableEventException("Notification event is missing eventId, eventType or userId");
        }

        notificationService.handle(event);
    }
}