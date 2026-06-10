package ru.practicum.ewm.stats.collector.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ewm.kafka.topics")
public class KafkaTopicProperties {

    private String userActions = "stats.user-actions.v1";
}
