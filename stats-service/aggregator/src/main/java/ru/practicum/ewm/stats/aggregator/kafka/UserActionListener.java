package ru.practicum.ewm.stats.aggregator.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.aggregator.service.EventSimilarityService;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.avro.codec.AvroCodec;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserActionListener {

    private final EventSimilarityService eventSimilarityService;
    private final EventSimilarityProducer producer;

    @KafkaListener(
            topics = "#{@kafkaTopicProperties.userActions}",
            groupId = "${spring.kafka.consumer.group-id:aggregator}"
    )
    public void onUserAction(byte[] payload) {
        UserActionAvro action = AvroCodec.deserialize(payload, UserActionAvro.class);
        for (EventSimilarityAvro similarity : eventSimilarityService.apply(action)) {
            producer.send(similarity);
            log.debug("Event similarity updated: {}-{}={}",
                    similarity.getEventA(), similarity.getEventB(), similarity.getScore());
        }
    }
}
