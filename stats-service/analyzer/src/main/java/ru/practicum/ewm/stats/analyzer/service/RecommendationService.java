package ru.practicum.ewm.stats.analyzer.service;

import lombok.RequiredArgsConstructor;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private static final long NON_EXISTING_EVENT_ID = -1L;

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

        Set<Long> interactedEventIds = new HashSet<>(userInteractionRepository.findEventIdsByUserId(userId));

        return eventSimilarityRepository
                .findSimilarUnseenEvents(eventId, idsForNotIn(interactedEventIds), limit)
                .stream()
                .map(projection -> new RecommendedEvent(projection.getEventId(), projection.getScore()))
                .toList();
    }

    public List<RecommendedEvent> getRecommendationsForUser(long userId, int maxResults) {
        int limit = normalizeLimit(maxResults);
        if (limit == 0) {
            return List.of();
        }

        List<Long> recentEventIds = userInteractionRepository.findEventIdsByUserIdOrderByLastTimestampDesc(
                userId,
                org.springframework.data.domain.PageRequest.of(0, limit)
        );
        if (recentEventIds.isEmpty()) {
            return List.of();
        }

        List<UserInteraction> allInteractions = userInteractionRepository.findAllByUserId(userId);
        Map<Long, UserInteraction> interactionsByEvent = allInteractions.stream()
                .collect(Collectors.toMap(UserInteraction::getEventId, Function.identity()));
        Set<Long> interactedEventIds = interactionsByEvent.keySet();
        Set<Long> recentEventIdSet = new HashSet<>(recentEventIds);

        List<Long> candidates = findCandidateEvents(recentEventIdSet, interactedEventIds, limit);
        if (candidates.isEmpty()) {
            return List.of();
        }

        Set<Long> candidateIds = new HashSet<>(candidates);
        Map<Long, List<EventSimilarity>> similaritiesByCandidate = eventSimilarityRepository
                .findTopBetweenCandidatesAndEvents(candidateIds, interactedEventIds, limit)
                .stream()
                .collect(Collectors.groupingBy(similarity -> candidateEventId(similarity, candidateIds)));

        return candidates.stream()
                .map(candidateId -> predictScore(
                        candidateId,
                        similaritiesByCandidate.getOrDefault(candidateId, List.of()),
                        interactionsByEvent
                ))
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
        return eventSimilarityRepository.findCandidateEvents(recentEventIds, idsForNotIn(interactedEventIds), limit)
                .stream()
                .map(EventSimilarityRepository.EventScoreProjection::getEventId)
                .toList();
    }

    private RecommendedEvent predictScore(long candidateId,
                                          List<EventSimilarity> nearestNeighbors,
                                          Map<Long, UserInteraction> interactionsByEvent) {
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

    private long candidateEventId(EventSimilarity similarity, Set<Long> candidateIds) {
        return candidateIds.contains(similarity.getEventA()) ? similarity.getEventA() : similarity.getEventB();
    }

    private Set<Long> idsForNotIn(Set<Long> ids) {
        return ids.isEmpty() ? Set.of(NON_EXISTING_EVENT_ID) : ids;
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
