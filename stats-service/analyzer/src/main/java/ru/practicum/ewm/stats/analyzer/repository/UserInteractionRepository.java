package ru.practicum.ewm.stats.analyzer.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.stats.analyzer.model.UserInteraction;
import ru.practicum.ewm.stats.analyzer.model.UserInteractionId;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserInteractionRepository extends JpaRepository<UserInteraction, UserInteractionId> {

    List<UserInteraction> findAllByUserId(Long userId);

    @Query("""
            SELECT interaction.eventId
            FROM UserInteraction interaction
            WHERE interaction.userId = :userId
            """)
    List<Long> findEventIdsByUserId(@Param("userId") Long userId);

    @Query("""
            SELECT interaction.eventId
            FROM UserInteraction interaction
            WHERE interaction.userId = :userId
            ORDER BY interaction.lastTimestamp DESC
            """)
    List<Long> findEventIdsByUserIdOrderByLastTimestampDesc(@Param("userId") Long userId, Pageable pageable);

    List<UserInteraction> findAllByUserIdOrderByLastTimestampDesc(Long userId, Pageable pageable);

    Optional<UserInteraction> findByUserIdAndEventId(Long userId, Long eventId);

    @Query("""
            SELECT interaction.eventId AS eventId, SUM(interaction.weight) AS score
            FROM UserInteraction interaction
            WHERE interaction.eventId IN :eventIds
            GROUP BY interaction.eventId
            """)
    List<EventScoreProjection> sumWeightsByEventIds(@Param("eventIds") Collection<Long> eventIds);

    interface EventScoreProjection {
        Long getEventId();

        Double getScore();
    }
}
