package com.hourlyhire.notification.template;

import com.hourlyhire.notification.enums.NotificationChannel;
import com.hourlyhire.notification.event.NotificationEventType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class NotificationTemplates {

    @Value("${app.password-reset.frontend-url:http://localhost:3000/reset-password}")
    private String resetUrl;

    @Value("${app.password-reset.expiration-minutes:15}")
    private long resetExpiryMinutes;

    /** Which channels each event type is delivered on. */
    public List<NotificationChannel> channelsFor(NotificationEventType type) {
        return switch (type) {
            case PASSWORD_RESET_REQUESTED -> List.of(NotificationChannel.EMAIL);
            case USER_REGISTERED -> List.of(NotificationChannel.EMAIL, NotificationChannel.IN_APP);
        };
    }

    public RenderedMessage render(NotificationEventType type, Map<String, String> data) {
        Map<String, String> values = data == null ? Map.of() : data;
        return switch (type) {
            case PASSWORD_RESET_REQUESTED -> new RenderedMessage(
                    "Reset your HourlyHire password",
                    "We received a request to reset your password. Use this link within "
                            + resetExpiryMinutes + " minutes: " + resetUrl + "?token="
                            + values.getOrDefault("token", "")
                            + "\n\nIf you did not request this, you can ignore this email.",
                    true);
            case USER_REGISTERED -> new RenderedMessage(
                    "Welcome to HourlyHire",
                    "Hi " + values.getOrDefault("username", "there")
                            + ", your HourlyHire account is ready.",
                    false);
        };
    }
}