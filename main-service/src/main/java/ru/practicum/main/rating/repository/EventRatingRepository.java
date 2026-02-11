package ru.practicum.main.rating.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.rating.model.EventRating;
import ru.practicum.main.rating.status.VoteType;

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
}
