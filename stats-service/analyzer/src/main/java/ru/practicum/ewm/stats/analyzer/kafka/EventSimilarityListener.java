package ru.practicum.ewm.stats.analyzer.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.analyzer.service.RecommendationService;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.codec.AvroCodec;

@Component
@RequiredArgsConstructor
public class EventSimilarityListener {

    private final RecommendationService recommendationService;

    @KafkaListener(
            topics = "${ewm.kafka.topics.events-similarity:stats.events-similarity.v1}",
            groupId = "${spring.kafka.consumer.events-similarity-group-id:analyzer-similarities}"
    )
    public void onEventSimilarity(byte[] payload) {
        EventSimilarityAvro similarity = AvroCodec.deserialize(payload, EventSimilarityAvro.class);
        recommendationService.applyEventSimilarity(similarity);
    }
}
