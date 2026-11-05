package com.hourlyhire.notification.channel;

import com.hourlyhire.notification.entity.Notification;
import com.hourlyhire.notification.enums.NotificationChannel;
import com.hourlyhire.notification.template.RenderedMessage;
import org.springframework.stereotype.Component;

@Component
public class InAppChannelSender implements NotificationChannelSender {

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.IN_APP;
    }

    // Nothing to deliver: the saved row IS the in-app notification the website reads.
    @Override
    public void send(Notification notification, RenderedMessage message) {
    }
}