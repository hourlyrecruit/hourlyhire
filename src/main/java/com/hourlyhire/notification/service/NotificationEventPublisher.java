package com.hourlyhire.notification.service;

import com.hourlyhire.notification.config.KafkaTopicProperties;
import com.hourlyhire.notification.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaTopicProperties topics;

    public void publish(NotificationEvent event) {
        String json = objectMapper.writeValueAsString(event);
        // Key = userId, so all events for one user go to the same partition and stay in order.
        kafkaTemplate.send(topics.notificationEvents(), String.valueOf(event.userId()), json)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event {} ({})", event.eventId(), event.eventType(), ex);
                    } else {
                        log.info("Published event {} ({}) to partition {}", event.eventId(),
                                event.eventType(), result.getRecordMetadata().partition());
                    }
                });
    }
}