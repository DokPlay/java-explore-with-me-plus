package ru.practicum.ewm.stats.analyzer.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.analyzer.service.RecommendationService;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.avro.codec.AvroCodec;

@Component
@RequiredArgsConstructor
public class UserActionListener {

    private final RecommendationService recommendationService;

    @KafkaListener(
            topics = "${ewm.kafka.topics.user-actions:stats.user-actions.v1}",
            groupId = "${spring.kafka.consumer.user-actions-group-id:analyzer-actions}"
    )
    public void onUserAction(byte[] payload) {
        UserActionAvro action = AvroCodec.deserialize(payload, UserActionAvro.class);
        recommendationService.applyUserAction(action);
    }
}
