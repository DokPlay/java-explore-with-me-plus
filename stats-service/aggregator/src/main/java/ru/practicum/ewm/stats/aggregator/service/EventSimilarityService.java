package ru.practicum.ewm.stats.aggregator.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.aggregator.model.EventPair;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class EventSimilarityService {

    private final Map<Long, Map<Long, Double>> userWeightsByEvent = new HashMap<>();
    private final Map<Long, Double> weightSumsByEvent = new HashMap<>();
    private final Map<EventPair, Double> minWeightSumsByPair = new HashMap<>();

    public synchronized List<EventSimilarityAvro> apply(UserActionAvro action) {
        long eventId = action.getEventId();
        long userId = action.getUserId();
        double actionWeight = toWeight(action.getActionType());
        double oldWeight = getUserWeight(eventId, userId);
        double newWeight = Math.max(oldWeight, actionWeight);

        if (Double.compare(newWeight, oldWeight) <= 0) {
            return List.of();
        }

        Set<Long> otherEventIds = new HashSet<>(userWeightsByEvent.keySet());
        otherEventIds.remove(eventId);

        userWeightsByEvent
                .computeIfAbsent(eventId, ignored -> new HashMap<>())
                .put(userId, newWeight);
        weightSumsByEvent.merge(eventId, newWeight - oldWeight, Double::sum);

        List<EventSimilarityAvro> updated = new ArrayList<>();
        for (long otherEventId : otherEventIds) {
            updatePairMinSum(eventId, otherEventId, userId, oldWeight, newWeight);
            double score = calculateScore(eventId, otherEventId);
            if (score > 0.0d) {
                EventPair pair = new EventPair(eventId, otherEventId);
                updated.add(EventSimilarityAvro.newBuilder()
                        .setEventA(pair.first())
                        .setEventB(pair.second())
                        .setScore(score)
                        .setTimestamp(action.getTimestamp())
                        .build());
            }
        }

        return updated;
    }

    private void updatePairMinSum(long eventId, long otherEventId, long userId, double oldWeight, double newWeight) {
        double otherWeight = getUserWeight(otherEventId, userId);
        if (otherWeight == 0.0d) {
            return;
        }

        EventPair pair = new EventPair(eventId, otherEventId);
        double oldContribution = Math.min(oldWeight, otherWeight);
        double newContribution = Math.min(newWeight, otherWeight);
        double delta = newContribution - oldContribution;
        if (delta != 0.0d) {
            minWeightSumsByPair.merge(pair, delta, Double::sum);
        }
    }

    private double calculateScore(long eventA, long eventB) {
        EventPair pair = new EventPair(eventA, eventB);
        double minWeightSum = minWeightSumsByPair.getOrDefault(pair, 0.0d);
        double eventASum = weightSumsByEvent.getOrDefault(eventA, 0.0d);
        double eventBSum = weightSumsByEvent.getOrDefault(eventB, 0.0d);

        if (minWeightSum == 0.0d || eventASum == 0.0d || eventBSum == 0.0d) {
            return 0.0d;
        }

        return minWeightSum / (Math.sqrt(eventASum) * Math.sqrt(eventBSum));
    }

    private double getUserWeight(long eventId, long userId) {
        return userWeightsByEvent
                .getOrDefault(eventId, Map.of())
                .getOrDefault(userId, 0.0d);
    }

    private double toWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> 0.4d;
            case REGISTER -> 0.8d;
            case LIKE -> 1.0d;
        };
    }
}
