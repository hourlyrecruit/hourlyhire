package com.hourlyhire.notification.service;

import com.hourlyhire.notification.channel.NotificationChannelSender;
import com.hourlyhire.notification.entity.Notification;
import com.hourlyhire.notification.enums.NotificationChannel;
import com.hourlyhire.notification.enums.NotificationStatus;
import com.hourlyhire.notification.event.NotificationEvent;
import com.hourlyhire.notification.exception.NonRetryableEventException;
import com.hourlyhire.notification.exception.NotificationDeliveryException;
import com.hourlyhire.notification.repository.NotificationRepository;
import com.hourlyhire.notification.template.NotificationTemplates;
import com.hourlyhire.notification.template.RenderedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationTemplates templates;
    private final Map<NotificationChannel, NotificationChannelSender> senders = new EnumMap<>(NotificationChannel.class);

    public NotificationService(NotificationRepository repository,
                               NotificationTemplates templates,
                               List<NotificationChannelSender> senderList) {
        this.repository = repository;
        this.templates = templates;
        senderList.forEach(sender -> senders.put(sender.channel(), sender));
    }

    // Deliberately NOT @Transactional: a FAILED status must be saved even though we then throw.
    public void handle(NotificationEvent event) {
        List<NotificationChannel> channels = templates.channelsFor(event.eventType());

        // Check everything first, so a bad event never half-delivers.
        for (NotificationChannel channel : channels) {
            if (channel != NotificationChannel.IN_APP && recipientFor(channel, event) == null) {
                throw new NonRetryableEventException(
                        "Event " + event.eventId() + " has no recipient for channel " + channel);
            }
        }

        RenderedMessage message = templates.render(event.eventType(), event.data());
        List<String> failures = new ArrayList<>();

        for (NotificationChannel channel : channels) {
            try {
                deliver(event, channel, message);
            } catch (Exception e) {
                failures.add(channel + ": " + e.getMessage());
            }
        }

        if (!failures.isEmpty()) {
            throw new NotificationDeliveryException(
                    "Event " + event.eventId() + " failed on " + String.join("; ", failures));
        }
    }

    private void deliver(NotificationEvent event, NotificationChannel channel, RenderedMessage message) {
        Notification notification = repository.findByEventIdAndChannel(event.eventId(), channel).orElse(null);

        if (notification == null) {
            notification = new Notification(event.eventId(), event.eventType(), event.userId(), channel,
                    recipientFor(channel, event), message.subject(), message.storedBody());
            try {
                notification = repository.saveAndFlush(notification);
            } catch (DataIntegrityViolationException e) {
                // Only a duplicate if the row for this event and channel now exists.
                if (repository.findByEventIdAndChannel(event.eventId(), channel).isPresent()) {
                    log.info("Event {} on {} is already being handled, skipping", event.eventId(), channel);
                    return;
                }
                throw e; // a real database problem: let Kafka retry, then dead-letter it
            }
        } else if (notification.getStatus() == NotificationStatus.SENT) {
            log.info("Event {} on {} already sent, skipping duplicate", event.eventId(), channel);
            return;
        }

        try {
            senders.get(channel).send(notification, message);
            notification.markSent(Instant.now());
            repository.save(notification);
            log.info("Event {} delivered on {}", event.eventId(), channel);
        } catch (Exception e) {
            notification.markFailed(e.getClass().getSimpleName() + ": " + e.getMessage());
            repository.save(notification);
            throw e;
        }
    }

    private String recipientFor(NotificationChannel channel, NotificationEvent event) {
        Map<String, String> data = event.data() == null ? Map.of() : event.data();
        String value = switch (channel) {
            case EMAIL -> data.get("email");
            case SMS -> data.get("phone");
            case IN_APP -> null;
        };
        return value == null || value.isBlank() ? null : value;
    }
}