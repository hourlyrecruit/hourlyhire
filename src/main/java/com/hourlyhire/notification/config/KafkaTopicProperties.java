package com.hourlyhire.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.kafka.topics")
public record KafkaTopicProperties(
        @DefaultValue("notification.events") String notificationEvents,
        @DefaultValue("notification.events.dlt") String notificationEventsDlt,
        @DefaultValue("3") int partitions,
        @DefaultValue("1") short replicas
) {
}