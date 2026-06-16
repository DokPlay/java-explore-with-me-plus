package ru.practicum.main.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.common.EwmHeaders;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for public access to events.
 */
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicEventController {

    private final EventService eventService;

    /**
     * Returns published events with filters and sorting (EVENT_DATE, VIEWS, RATING).
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size,
            HttpServletRequest request) {
        log.info("GET /events - Публичный поиск событий: text={}, categories={}", text, categories);
        return eventService.searchPublicEvents(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request);
    }

    /**
     * Returns personalized event recommendations.
     */
    @GetMapping("/recommendations")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getRecommendations(
            @RequestHeader(EwmHeaders.USER_ID) Long userId,
            @RequestParam(defaultValue = "10") @Positive int maxResults) {
        log.info("GET /events/recommendations - Рекомендации для пользователя userId={}", userId);
        return eventService.getRecommendations(userId, maxResults);
    }

    /**
     * Returns a published event by identifier.
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(
            @PathVariable Long id,
            @RequestHeader(value = EwmHeaders.USER_ID, required = false) Long userId,
            HttpServletRequest request) {
        log.info("GET /events/{} - Получение опубликованного события пользователем userId={}", id, userId);
        return eventService.getPublishedEventById(id, userId, request);
    }

    /**
     * Sends a like action for a visited event.
     */
    @PutMapping("/{eventId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void likeEvent(
            @PathVariable Long eventId,
            @RequestHeader(EwmHeaders.USER_ID) Long userId) {
        log.info("PUT /events/{}/like - Лайк пользователя userId={}", eventId, userId);
        eventService.likeEvent(userId, eventId);
    }
}
