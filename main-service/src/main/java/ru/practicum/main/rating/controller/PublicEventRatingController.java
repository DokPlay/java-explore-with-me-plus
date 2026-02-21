package ru.practicum.main.rating.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.rating.dto.EventRatingSummaryDto;
import ru.practicum.main.rating.service.EventRatingService;

/**
 * Public API for event rating summary.
 */
@RestController
@RequestMapping("/events/{eventId}/rating")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicEventRatingController {

    private final EventRatingService eventRatingService;

    /**
     * Returns rating summary for a published event.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public EventRatingSummaryDto getEventRating(@PathVariable @Positive Long eventId) {
        log.info("GET /events/{}/rating - Получение рейтинга события", eventId);
        return eventRatingService.getEventRating(eventId);
    }
}
