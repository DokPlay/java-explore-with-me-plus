package ru.practicum.main.moderation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.moderation.model.EventModerationLog;

/**
 * Repository for moderation history.
 */
@Repository
public interface EventModerationLogRepository extends JpaRepository<EventModerationLog, Long> {

    /**
     * Returns moderation history entries for event.
     */
    Page<EventModerationLog> findAllByEventId(Long eventId, Pageable pageable);
}
