package ru.practicum.ewm.stats.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.analyzer.model.EventSimilarity;
import ru.practicum.ewm.stats.analyzer.model.EventSimilarityId;
import ru.practicum.ewm.stats.analyzer.model.UserInteraction;
import ru.practicum.ewm.stats.analyzer.repository.EventSimilarityRepository;
import ru.practicum.ewm.stats.analyzer.repository.UserInteractionRepository;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final UserInteractionRepository userInteractionRepository;
    private final EventSimilarityRepository eventSimilarityRepository;

    @Transactional
    public void applyUserAction(UserActionAvro action) {
        double actionWeight = toWeight(action.getActionType());
        UserInteraction interaction = userInteractionRepository
                .findByUserIdAndEventId(action.getUserId(), action.getEventId())
                .orElseGet(() -> UserInteraction.builder()
                        .userId(action.getUserId())
                        .eventId(action.getEventId())
                        .weight(0.0d)
                        .lastTimestamp(action.getTimestamp())
                        .build());

        interaction.setWeight(Math.max(interaction.getWeight(), actionWeight));
        if (interaction.getLastTimestamp() == null
                || action.getTimestamp().isAfter(interaction.getLastTimestamp())) {
            interaction.setLastTimestamp(action.getTimestamp());
        }

        userInteractionRepository.save(interaction);
    }

    @Transactional
    public void applyEventSimilarity(EventSimilarityAvro similarityAvro) {
        long first = Math.min(similarityAvro.getEventA(), similarityAvro.getEventB());
        long second = Math.max(similarityAvro.getEventA(), similarityAvro.getEventB());
        EventSimilarityId id = new EventSimilarityId(first, second);

        EventSimilarity similarity = eventSimilarityRepository.findById(id)
                .orElseGet(() -> EventSimilarity.builder()
                        .eventA(first)
                        .eventB(second)
                        .build());

        similarity.setScore(similarityAvro.getScore());
        similarity.setUpdatedAt(similarityAvro.getTimestamp());
        eventSimilarityRepository.save(similarity);
    }

    public List<RecommendedEvent> getSimilarEvents(long eventId, long userId, int maxResults) {
        int limit = normalizeLimit(maxResults);
        if (limit == 0) {
            return List.of();
        }

        Set<Long> interactedEventIds = userInteractionRepository.findAllByUserId(userId).stream()
                .map(UserInteraction::getEventId)
                .collect(Collectors.toSet());

        return eventSimilarityRepository.findAllByEventAOrEventB(eventId, eventId).stream()
                .map(similarity -> new RecommendedEvent(similarity.otherEvent(eventId), similarity.getScore()))
                .filter(recommended -> !interactedEventIds.contains(recommended.eventId()))
                .sorted(Comparator.comparing(RecommendedEvent::score).reversed()
                        .thenComparing(RecommendedEvent::eventId))
                .limit(limit)
                .toList();
    }

    public List<RecommendedEvent> getRecommendationsForUser(long userId, int maxResults) {
        int limit = normalizeLimit(maxResults);
        if (limit == 0) {
            return List.of();
        }

        List<UserInteraction> recentInteractions = userInteractionRepository
                .findAllByUserIdOrderByLastTimestampDesc(userId, PageRequest.of(0, limit));
        if (recentInteractions.isEmpty()) {
            return List.of();
        }

        List<UserInteraction> allInteractions = userInteractionRepository.findAllByUserId(userId);
        Map<Long, UserInteraction> interactionsByEvent = allInteractions.stream()
                .collect(Collectors.toMap(UserInteraction::getEventId, Function.identity()));
        Set<Long> interactedEventIds = interactionsByEvent.keySet();
        Set<Long> recentEventIds = recentInteractions.stream()
                .map(UserInteraction::getEventId)
                .collect(Collectors.toSet());

        List<Long> candidates = findCandidateEvents(recentEventIds, interactedEventIds, limit);
        return candidates.stream()
                .map(candidateId -> predictScore(candidateId, interactedEventIds, interactionsByEvent, limit))
                .filter(recommended -> recommended.score() > 0.0d)
                .sorted(Comparator.comparing(RecommendedEvent::score).reversed()
                        .thenComparing(RecommendedEvent::eventId))
                .limit(limit)
                .toList();
    }

    public List<RecommendedEvent> getInteractionsCount(Collection<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return List.of();
        }

        Map<Long, Double> scoresByEvent = userInteractionRepository.sumWeightsByEventIds(eventIds).stream()
                .collect(Collectors.toMap(
                        UserInteractionRepository.EventScoreProjection::getEventId,
                        projection -> projection.getScore() == null ? 0.0d : projection.getScore()
                ));

        return eventIds.stream()
                .distinct()
                .map(eventId -> new RecommendedEvent(eventId, scoresByEvent.getOrDefault(eventId, 0.0d)))
                .toList();
    }

    private List<Long> findCandidateEvents(Set<Long> recentEventIds, Set<Long> interactedEventIds, int limit) {
        Map<Long, Double> bestSimilarityByCandidate = new LinkedHashMap<>();

        for (EventSimilarity similarity : eventSimilarityRepository.findAllInvolvingAny(recentEventIds)) {
            Long candidateId = null;
            if (recentEventIds.contains(similarity.getEventA())) {
                candidateId = similarity.getEventB();
            } else if (recentEventIds.contains(similarity.getEventB())) {
                candidateId = similarity.getEventA();
            }

            if (candidateId != null && !interactedEventIds.contains(candidateId)) {
                bestSimilarityByCandidate.merge(candidateId, similarity.getScore(), Math::max);
            }
        }

        return bestSimilarityByCandidate.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed()
                        .thenComparing(Map.Entry::getKey))
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }

    private RecommendedEvent predictScore(long candidateId,
                                          Set<Long> interactedEventIds,
                                          Map<Long, UserInteraction> interactionsByEvent,
                                          int nearestNeighborsLimit) {
        List<EventSimilarity> nearestNeighbors = eventSimilarityRepository
                .findAllBetweenCandidateAndEvents(candidateId, interactedEventIds)
                .stream()
                .sorted(Comparator.comparing(EventSimilarity::getScore).reversed())
                .limit(nearestNeighborsLimit)
                .toList();

        double weightedScoreSum = 0.0d;
        double similaritySum = 0.0d;
        for (EventSimilarity similarity : nearestNeighbors) {
            long neighborEventId = similarity.otherEvent(candidateId);
            UserInteraction interaction = interactionsByEvent.get(neighborEventId);
            if (interaction != null) {
                weightedScoreSum += similarity.getScore() * interaction.getWeight();
                similaritySum += similarity.getScore();
            }
        }

        double predictedScore = similaritySum == 0.0d ? 0.0d : weightedScoreSum / similaritySum;
        return new RecommendedEvent(candidateId, predictedScore);
    }

    private double toWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> 0.4d;
            case REGISTER -> 0.8d;
            case LIKE -> 1.0d;
        };
    }

    private int normalizeLimit(int maxResults) {
        return maxResults <= 0 ? 0 : Math.min(maxResults, 100);
    }
}
