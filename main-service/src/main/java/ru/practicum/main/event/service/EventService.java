package ru.practicum.main.event.service;

import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.event.model.EventState;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс сервиса для работы с событиями.
 */
public interface EventService {

    // ========== Private API ==========

    /**
     * Получить события пользователя.
     */
    List<EventShortDto> getUserEvents(Long userId, int from, int size);

    /**
     * Создать новое событие.
     */
    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    /**
     * Получить полную информацию о событии пользователя.
     */
    EventFullDto getUserEventById(Long userId, Long eventId);

    /**
     * Обновить событие пользователем.
     */
    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    // ========== Admin API ==========

    /**
     * Поиск событий для администратора.
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
     * Обновить событие администратором.
     */
    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest);

    // ========== Public API ==========

    /**
     * Публичный поиск событий.
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
     * Получить опубликованное событие по ID.
     */
    EventFullDto getPublishedEventById(Long eventId, HttpServletRequest request);

    /**
     * Найти событие по ID (для внутреннего использования).
     */
    EventFullDto getEventById(Long eventId);
}
