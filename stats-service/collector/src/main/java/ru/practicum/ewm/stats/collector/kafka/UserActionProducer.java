package ru.practicum.ewm.stats.collector.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.avro.codec.AvroCodec;
import ru.practicum.ewm.stats.collector.config.KafkaTopicProperties;

@Component
@RequiredArgsConstructor
public class UserActionProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final KafkaTopicProperties topicProperties;

    public void send(UserActionAvro action) {
        kafkaTemplate.send(topicProperties.getUserActions(), key(action), AvroCodec.serialize(action));
    }

    private String key(UserActionAvro action) {
        return action.getUserId() + ":" + action.getEventId();
    }
}
