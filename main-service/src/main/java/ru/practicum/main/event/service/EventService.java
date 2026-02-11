package ru.practicum.main.event.service;

import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.moderation.dto.EventModerationLogDto;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for working with events.
 * <p>
 * Provides methods for three access levels:
 * <ul>
 *     <li><b>Private API</b> - operations for authorized users</li>
 *     <li><b>Admin API</b> - administrative operations</li>
 *     <li><b>Public API</b> - public access without authorization</li>
 * </ul>
 *
 * @author ExploreWithMe Team
 * @version 1.0
 */
public interface EventService {

        // Private API — operations for authorized event owners

        /**
         * Gets a list of events created by a user.
         *
         * @param userId user ID
         * @param from   start index for pagination
         * @param size   number of items per page
         * @return list of short event DTOs
         */
    List<EventShortDto> getUserEvents(Long userId, int from, int size);

        /**
         * Creates a new event.
         * <p>
         * The event is created in {@link EventState#PENDING} and requires
         * admin moderation before publication.
         *
         * @param userId      initiator user ID
         * @param newEventDto new event data
         * @return full DTO of the created event
         * @throws ru.practicum.main.exception.NotFoundException   if the user or category is not found
         * @throws ru.practicum.main.exception.ValidationException if the event date is invalid
         */
    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

        /**
         * Gets full information about a user's event.
         *
         * @param userId  user ID
         * @param eventId event ID
         * @return full event DTO
         * @throws ru.practicum.main.exception.NotFoundException if the event is not found
         */
    EventFullDto getUserEventById(Long userId, Long eventId);

        /**
         * Updates an event by a user.
         * <p>
         * A user can update only events in states:
         * {@link EventState#PENDING} or {@link EventState#CANCELED}.
         *
         * @param userId        user ID
         * @param eventId       event ID
         * @param updateRequest update data
         * @return full DTO of the updated event
         * @throws ru.practicum.main.exception.ConflictException if the event is already published
         */
    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

        // Admin API — moderation and management operations for events

        /**
         * Searches events with filters (for admins).
         * <p>
         * Returns full information about events, including unpublished ones.
         *
         * @param users      list of user IDs (optional)
         * @param states     list of event states (optional)
         * @param categories list of category IDs (optional)
         * @param rangeStart start of time range (optional)
         * @param rangeEnd   end of time range (optional)
         * @param from       start index for pagination
         * @param size       number of items per page
         * @return list of full event DTOs
         */
    List<EventFullDto> searchEventsForAdmin(
            List<Long> users,
            List<EventState> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size);

        /**
         * Updates an event by an administrator.
         * <p>
         * An administrator can:
         * <ul>
         *     <li>Publish an event (PUBLISH_EVENT) - only for PENDING status</li>
         *     <li>Reject an event (REJECT_EVENT) - only for unpublished events</li>
         *     <li>Change any event fields</li>
         * </ul>
         *
         * @param eventId       event ID
         * @param updateRequest update data
         * @return full DTO of the updated event
         * @throws ru.practicum.main.exception.ConflictException if business rules are violated
         */
    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest);

        /**
         * Returns event moderation history for administrators.
         *
         * @param eventId event id
         * @param from    start index for pagination
         * @param size    page size
         * @return list of moderation history entries
         */
    List<EventModerationLogDto> getEventModerationHistory(Long eventId, int from, int size);

        // Public API — access to published events without authorization

        /**
         * Public search for events.
         * <p>
         * Returns only published events ({@link EventState#PUBLISHED}).
         * Automatically records view statistics.
         *
         * @param text          search text in title and description (optional)
         * @param categories    list of category IDs (optional)
         * @param paid          paid filter (optional)
         * @param rangeStart    start of time range (optional)
         * @param rangeEnd      end of time range (optional)
         * @param onlyAvailable only events with available spots
         * @param sort          sorting: EVENT_DATE or VIEWS
         * @param from          start index for pagination
         * @param size          number of items per page
         * @param request       HTTP request to obtain client IP
         * @return list of short event DTOs
         */
    List<EventShortDto> searchPublicEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            int from,
            int size,
            HttpServletRequest request);

        /**
         * Gets a published event by ID.
         * <p>
         * Automatically records a view hit and returns
         * the current view count from Stats Service.
         *
         * @param eventId event ID
         * @param request HTTP request to obtain client IP
         * @return full event DTO
         * @throws ru.practicum.main.exception.NotFoundException if the event is not found or not published
         */
    EventFullDto getPublishedEventById(Long eventId, HttpServletRequest request);

        /**
         * Gets an event by ID (for internal use).
         * <p>
         * Returns the event regardless of publication status.
         * Does not record view statistics.
         *
         * @param eventId event ID
         * @return full event DTO
         * @throws ru.practicum.main.exception.NotFoundException if the event is not found
         */
    EventFullDto getEventById(Long eventId);
}
