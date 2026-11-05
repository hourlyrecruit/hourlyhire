package com.hourlyhire.notification.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableConfigurationProperties(KafkaTopicProperties.class)
public class KafkaTopicConfig {

    @Bean
    NewTopic notificationEventsTopic(KafkaTopicProperties props) {
        return TopicBuilder.name(props.notificationEvents())
                .partitions(props.partitions())
                .replicas(props.replicas())
                .build();
    }

    // Events that still fail after all retries are parked here for inspection.
    @Bean
    NewTopic notificationEventsDltTopic(KafkaTopicProperties props) {
        return TopicBuilder.name(props.notificationEventsDlt())
                .partitions(props.partitions())
                .replicas(props.replicas())
                .build();
    }
}