package ru.practicum.main.rating.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.rating.dto.EventVoteDto;
import ru.practicum.main.rating.dto.EventVoteRequest;
import ru.practicum.main.rating.service.EventRatingService;

import java.util.List;

/**
 * Private API for user event votes.
 */
@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventRatingController {

    private final EventRatingService eventRatingService;

    /**
     * Creates or updates user vote for event.
     */
    @PutMapping("/events/{eventId}/rating")
    @ResponseStatus(HttpStatus.OK)
    public EventVoteDto upsertVote(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody EventVoteRequest request) {
        log.info("PUT /users/{}/events/{}/rating - Голосование", userId, eventId);
        return eventRatingService.upsertVote(userId, eventId, request);
    }

    /**
     * Deletes user vote for event.
     */
    @DeleteMapping("/events/{eventId}/rating")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVote(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId) {
        log.info("DELETE /users/{}/events/{}/rating - Удаление голоса", userId, eventId);
        eventRatingService.deleteVote(userId, eventId);
    }

    /**
     * Returns votes created by user.
     */
    @GetMapping("/ratings")
    @ResponseStatus(HttpStatus.OK)
    public List<EventVoteDto> getUserVotes(
            @PathVariable @Positive Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET /users/{}/ratings - Получение голосов пользователя", userId);
        return eventRatingService.getUserVotes(userId, from, size);
    }
}
