package ru.practicum.main.rating.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.rating.dto.EventRatingSummaryDto;
import ru.practicum.main.rating.dto.EventVoteDto;
import ru.practicum.main.rating.dto.EventVoteRequest;
import ru.practicum.main.rating.mapper.EventRatingMapper;
import ru.practicum.main.rating.model.EventRating;
import ru.practicum.main.rating.repository.EventRatingRepository;
import ru.practicum.main.rating.status.VoteType;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;
import ru.practicum.main.util.PaginationValidator;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Rating service implementation for likes/dislikes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventRatingServiceImpl implements EventRatingService {

    private final EventRatingRepository eventRatingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventRatingMapper eventRatingMapper;

    @Override
    @Transactional
    public EventVoteDto upsertVote(Long userId, Long eventId, EventVoteRequest request) {
        log.info("Пользователь userId={} голосует за событие eventId={} голосом={}",
                userId, eventId, request.getVote());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + eventId));

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Оценивать можно только опубликованные события");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может оценивать своё событие");
        }

        EventRating rating = eventRatingRepository.findByUserIdAndEventId(userId, eventId)
                .orElseGet(() -> EventRating.builder()
                        .event(event)
                        .user(user)
                        .createdOn(nowTruncatedToMillis())
                        .build());

        rating.setVote(request.getVote());
        rating.setUpdatedOn(nowTruncatedToMillis());

        EventRating saved = eventRatingRepository.save(rating);
        return eventRatingMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteVote(Long userId, Long eventId) {
        log.info("Удаление голоса userId={} для события eventId={}", userId, eventId);
        validateUserExists(userId);
        validateEventExists(eventId);

        EventRating rating = eventRatingRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException("Голос пользователя для события не найден"));
        eventRatingRepository.delete(rating);
    }

    @Override
    public EventRatingSummaryDto getEventRating(Long eventId) {
        log.info("Получение рейтинга события eventId={}", eventId);
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Опубликованное событие не найдено: id=" + eventId));

        Long likes = normalizeCount(eventRatingRepository.countByEventIdAndVote(eventId, VoteType.LIKE));
        Long dislikes = normalizeCount(eventRatingRepository.countByEventIdAndVote(eventId, VoteType.DISLIKE));

        return EventRatingSummaryDto.builder()
                .eventId(event.getId())
                .likes(likes)
                .dislikes(dislikes)
                .score(likes - dislikes)
                .build();
    }

    @Override
    public List<EventVoteDto> getUserVotes(Long userId, int from, int size) {
        log.info("Получение голосов пользователя userId={}, from={}, size={}", userId, from, size);
        PaginationValidator.validatePagination(from, size);
        validateUserExists(userId);

        Pageable pageable = PageRequest.of(
                from / size,
                size,
                Sort.by("updatedOn").descending().and(Sort.by("id").descending())
        );
        List<EventRating> votes = eventRatingRepository.findAllByUserId(userId, pageable)
                .getContent();
        return eventRatingMapper.toDtoList(votes);
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден: id=" + userId);
        }
    }

    private void validateEventExists(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие не найдено: id=" + eventId);
        }
    }

    private Long normalizeCount(Long value) {
        return value == null ? 0L : value;
    }

    private LocalDateTime nowTruncatedToMillis() {
        return LocalDateTime.now()
                .truncatedTo(ChronoUnit.MILLIS);
    }
}
