package com.hourlyhire.notification.channel;

import com.hourlyhire.notification.entity.Notification;
import com.hourlyhire.notification.enums.NotificationChannel;
import com.hourlyhire.notification.template.RenderedMessage;

public interface NotificationChannelSender {

    NotificationChannel channel();

    /** Deliver the message. Throw any exception if delivery fails. */
    void send(Notification notification, RenderedMessage message);
}