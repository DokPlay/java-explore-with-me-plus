package ru.practicum.ewm.stats.collector.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.avro.codec.AvroCodec;

@Component
@RequiredArgsConstructor
public class UserActionProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Value("${ewm.kafka.topics.user-actions:stats.user-actions.v1}")
    private String userActionsTopic;

    public void send(UserActionAvro action) {
        kafkaTemplate.send(userActionsTopic, key(action), AvroCodec.serialize(action));
    }

    private String key(UserActionAvro action) {
        return action.getUserId() + ":" + action.getEventId();
    }
}
