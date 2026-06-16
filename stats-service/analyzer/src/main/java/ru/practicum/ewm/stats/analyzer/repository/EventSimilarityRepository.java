package ru.practicum.ewm.stats.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.stats.analyzer.model.EventSimilarity;
import ru.practicum.ewm.stats.analyzer.model.EventSimilarityId;

import java.util.Collection;
import java.util.List;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, EventSimilarityId> {

    List<EventSimilarity> findAllByEventAOrEventB(Long eventA, Long eventB);

    @Query("""
            SELECT similarity FROM EventSimilarity similarity
            WHERE similarity.eventA IN :eventIds OR similarity.eventB IN :eventIds
            """)
    List<EventSimilarity> findAllInvolvingAny(@Param("eventIds") Collection<Long> eventIds);

    @Query(value = """
            SELECT
                CASE WHEN event_a = :eventId THEN event_b ELSE event_a END AS "eventId",
                score AS "score"
            FROM event_similarities
            WHERE (event_a = :eventId OR event_b = :eventId)
              AND (CASE WHEN event_a = :eventId THEN event_b ELSE event_a END) NOT IN (:excludedEventIds)
            ORDER BY score DESC, "eventId" ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<EventScoreProjection> findSimilarUnseenEvents(
            @Param("eventId") Long eventId,
            @Param("excludedEventIds") Collection<Long> excludedEventIds,
            @Param("limit") int limit);

    @Query(value = """
            SELECT
                candidate_id AS "eventId",
                MAX(score) AS "score"
            FROM (
                SELECT
                    CASE WHEN event_a IN (:sourceEventIds) THEN event_b ELSE event_a END AS candidate_id,
                    score
                FROM event_similarities
                WHERE event_a IN (:sourceEventIds) OR event_b IN (:sourceEventIds)
            ) candidates
            WHERE candidate_id NOT IN (:excludedEventIds)
            GROUP BY candidate_id
            ORDER BY MAX(score) DESC, candidate_id ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<EventScoreProjection> findCandidateEvents(
            @Param("sourceEventIds") Collection<Long> sourceEventIds,
            @Param("excludedEventIds") Collection<Long> excludedEventIds,
            @Param("limit") int limit);

    @Query(value = """
            SELECT event_a, event_b, score, updated_at
            FROM (
                SELECT
                    similarity.*,
                    ROW_NUMBER() OVER (
                        PARTITION BY CASE
                            WHEN similarity.event_a IN (:candidateIds) THEN similarity.event_a
                            ELSE similarity.event_b
                        END
                        ORDER BY similarity.score DESC
                    ) AS neighbor_rank
                FROM event_similarities similarity
                WHERE (similarity.event_a IN (:candidateIds) AND similarity.event_b IN (:eventIds))
                   OR (similarity.event_b IN (:candidateIds) AND similarity.event_a IN (:eventIds))
            ) ranked
            WHERE neighbor_rank <= :limit
            """, nativeQuery = true)
    List<EventSimilarity> findTopBetweenCandidatesAndEvents(
            @Param("candidateIds") Collection<Long> candidateIds,
            @Param("eventIds") Collection<Long> eventIds,
            @Param("limit") int limit);

    interface EventScoreProjection {
        Long getEventId();

        Double getScore();
    }
}
