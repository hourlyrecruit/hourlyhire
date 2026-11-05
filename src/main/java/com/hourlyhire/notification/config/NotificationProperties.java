package com.hourlyhire.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.notification")
public record NotificationProperties(
        @DefaultValue("no-reply@hourlyhire.local") String mailFrom,
        @DefaultValue("false") boolean devLogOnly
) {
}