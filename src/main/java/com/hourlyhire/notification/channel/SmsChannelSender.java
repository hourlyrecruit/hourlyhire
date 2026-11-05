package com.hourlyhire.notification.channel;

import com.hourlyhire.notification.config.NotificationProperties;
import com.hourlyhire.notification.entity.Notification;
import com.hourlyhire.notification.enums.NotificationChannel;
import com.hourlyhire.notification.template.RenderedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsChannelSender implements NotificationChannelSender {

    private final NotificationProperties properties;

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.SMS;
    }

    @Override
    public void send(Notification notification, RenderedMessage message) {
        if (properties.devLogOnly()) {
            log.info("[DEV ONLY] SMS to {} | {}", notification.getRecipient(), message.body());
            return;
        }
        // Later: call the real SMS provider (Twilio, MSG91, ...) here.
        throw new IllegalStateException("No SMS provider configured");
    }
}