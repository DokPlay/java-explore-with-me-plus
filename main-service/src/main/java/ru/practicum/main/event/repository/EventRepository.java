package ru.practicum.main.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for events.
 * <p>
 * Contains methods for user, admin, and public search.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

        /**
         * Returns a user's events with pagination.
         *
         * @param initiatorId initiator user ID
         * @param pageable    pagination parameters
         * @return page of the user's events
         */
    Page<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

        /**
         * Finds an event by ID and initiator.
         *
         * @param eventId     event ID
         * @param initiatorId initiator ID
         * @return event if found and belongs to the user
         */
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

        /**
         * Checks whether there are events in the specified category.
         *
         * @param categoryId category ID
         * @return true if events exist in the category
         */
    boolean existsByCategoryId(Long categoryId);

        /**
         * Searches events for admins with filters.
         *
         * @param users      list of user IDs (NULL = all)
         * @param states     list of states (NULL = all)
         * @param categories list of category IDs (NULL = all)
         * @param rangeStart start of time range (NULL = no limit)
         * @param rangeEnd   end of time range (NULL = no limit)
         * @param pageable   pagination parameters
         * @return page of events matching the filters
         */
    @Query("SELECT e FROM Event e " +
           "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
           "AND (:states IS NULL OR e.state IN :states) " +
           "AND (:categories IS NULL OR e.category.id IN :categories) " +
           "AND (CAST(:rangeStart AS timestamp) IS NULL OR e.eventDate >= :rangeStart) " +
           "AND (CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate <= :rangeEnd)")
    Page<Event> findEventsForAdmin(
            @Param("users") List<Long> users,
            @Param("states") List<EventState> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);

        /**
         * Public search for published events with filters.
         *
         * @param text          search text (NULL = no text filter)
         * @param categories    list of category IDs (NULL = all categories)
         * @param paid          paid filter (NULL = all)
         * @param rangeStart    start of time range
         * @param rangeEnd      end of time range
         * @param onlyAvailable only events with available spots
         * @param pageable      pagination parameters
         * @return page of published events
         */
    @Query("SELECT e FROM Event e " +
           "WHERE e.state = 'PUBLISHED' " +
           "AND (CAST(:text AS string) IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', CAST(:text AS string), '%')) " +
           "OR LOWER(e.description) LIKE LOWER(CONCAT('%', CAST(:text AS string), '%'))) " +
           "AND (:categories IS NULL OR e.category.id IN :categories) " +
           "AND (:paid IS NULL OR e.paid = :paid) " +
           "AND (CAST(:rangeStart AS timestamp) IS NULL OR e.eventDate >= :rangeStart) " +
           "AND (CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate <= :rangeEnd) " +
           "AND (:onlyAvailable = false OR e.participantLimit = 0 " +
           "OR e.confirmedRequests < e.participantLimit)")
    Page<Event> findPublicEvents(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") Boolean onlyAvailable,
            Pageable pageable);

        /**
         * Finds an event by ID and state.
         *
         * @param id    event ID
         * @param state required event state
         * @return event if found with the specified state
         */
    Optional<Event> findByIdAndState(Long id, EventState state);

}
