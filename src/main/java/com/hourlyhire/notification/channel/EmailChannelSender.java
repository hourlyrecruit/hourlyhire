package com.hourlyhire.notification.channel;

import com.hourlyhire.notification.config.NotificationProperties;
import com.hourlyhire.notification.entity.Notification;
import com.hourlyhire.notification.enums.NotificationChannel;
import com.hourlyhire.notification.template.RenderedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailChannelSender implements NotificationChannelSender {

    // Only exists when spring.mail.host is configured.
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final NotificationProperties properties;

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public void send(Notification notification, RenderedMessage message) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            if (properties.devLogOnly()) {
                log.info("[DEV ONLY] Email to {} | {} | {}",
                        notification.getRecipient(), message.subject(), message.body());
                return;
            }
            throw new IllegalStateException("SMTP is not configured (spring.mail.host)");
        }

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(properties.mailFrom());
        mail.setTo(notification.getRecipient());
        mail.setSubject(message.subject());
        mail.setText(message.body());
        mailSender.send(mail);
    }
}