package ru.practicum.main.rating.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.main.rating.model.EventRating;
import ru.practicum.main.rating.status.VoteType;

import java.util.List;
import java.util.Optional;

/**
 * Repository for event votes.
 */
@Repository
public interface EventRatingRepository extends JpaRepository<EventRating, Long> {

    /**
     * Finds vote by user and event.
     */
    Optional<EventRating> findByUserIdAndEventId(Long userId, Long eventId);

    /**
     * Returns paginated user votes.
     */
    Page<EventRating> findAllByUserId(Long userId, Pageable pageable);

    /**
     * Counts votes for event by type.
     */
    Long countByEventIdAndVote(Long eventId, VoteType vote);

    /**
     * Aggregated rating score (likes - dislikes) for a list of events.
     */
    @Query("SELECT r.event.id AS eventId, " +
            "SUM(CASE WHEN r.vote = :likeVote THEN 1 WHEN r.vote = :dislikeVote THEN -1 ELSE 0 END) AS score " +
            "FROM EventRating r " +
            "WHERE r.event.id IN :eventIds " +
            "GROUP BY r.event.id")
    List<EventScoreProjection> findScoresByEventIds(
            @Param("eventIds") List<Long> eventIds,
            @Param("likeVote") VoteType likeVote,
            @Param("dislikeVote") VoteType dislikeVote
    );

    interface EventScoreProjection {
        Long getEventId();

        Number getScore();
    }
}
