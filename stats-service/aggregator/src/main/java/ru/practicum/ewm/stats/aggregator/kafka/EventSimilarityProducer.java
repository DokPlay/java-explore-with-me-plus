package ru.practicum.ewm.stats.aggregator.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.codec.AvroCodec;

@Component
@RequiredArgsConstructor
public class EventSimilarityProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Value("${ewm.kafka.topics.events-similarity:stats.events-similarity.v1}")
    private String eventSimilarityTopic;

    public void send(EventSimilarityAvro similarity) {
        kafkaTemplate.send(eventSimilarityTopic, key(similarity), AvroCodec.serialize(similarity));
    }

    private String key(EventSimilarityAvro similarity) {
        return similarity.getEventA() + ":" + similarity.getEventB();
    }
}
