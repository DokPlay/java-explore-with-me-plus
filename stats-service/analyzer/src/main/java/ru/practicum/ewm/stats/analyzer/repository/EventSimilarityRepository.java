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

    @Query("""
            SELECT similarity FROM EventSimilarity similarity
            WHERE (similarity.eventA = :candidateId AND similarity.eventB IN :eventIds)
            OR (similarity.eventB = :candidateId AND similarity.eventA IN :eventIds)
            """)
    List<EventSimilarity> findAllBetweenCandidateAndEvents(
            @Param("candidateId") Long candidateId,
            @Param("eventIds") Collection<Long> eventIds);
}
