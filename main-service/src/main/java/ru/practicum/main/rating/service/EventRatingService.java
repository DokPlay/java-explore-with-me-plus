package ru.practicum.main.rating.service;

import ru.practicum.main.rating.dto.EventRatingSummaryDto;
import ru.practicum.main.rating.dto.EventVoteDto;
import ru.practicum.main.rating.dto.EventVoteRequest;

import java.util.List;

/**
 * Business service for event ratings.
 */
public interface EventRatingService {

    /**
     * Creates or updates user vote for event.
     */
    EventVoteDto upsertVote(Long userId, Long eventId, EventVoteRequest request);

    /**
     * Deletes user vote for event.
     */
    void deleteVote(Long userId, Long eventId);

    /**
     * Returns public event rating summary.
     */
    EventRatingSummaryDto getEventRating(Long eventId);

    /**
     * Returns votes created by user.
     */
    List<EventVoteDto> getUserVotes(Long userId, int from, int size);
}
