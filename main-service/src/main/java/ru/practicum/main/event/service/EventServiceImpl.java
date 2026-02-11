package ru.practicum.main.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.location.model.ManagedLocation;
import ru.practicum.main.location.repository.ManagedLocationRepository;
import ru.practicum.main.moderation.dto.EventModerationLogDto;
import ru.practicum.main.moderation.mapper.EventModerationLogMapper;
import ru.practicum.main.moderation.model.EventModerationLog;
import ru.practicum.main.moderation.repository.EventModerationLogRepository;
import ru.practicum.main.moderation.status.EventModerationAction;
import ru.practicum.main.rating.repository.EventRatingRepository;
import ru.practicum.main.rating.status.VoteType;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;
import ru.practicum.main.util.PaginationValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the event service.
 * <p>
 * Core business-logic class of the Events module. Handles:
 * <ul>
 *     <li>CRUD operations on events</li>
 *     <li>Event lifecycle (PENDING → PUBLISHED/CANCELED)</li>
 *     <li>Integration with Stats Service for view statistics</li>
 *     <li>Business-rule validation (time before event, access rights)</li>
 * </ul>
 *
 * <h2>Event lifecycle:</h2>
 * <pre>
 * [Creation] → PENDING → [Moderation] → PUBLISHED
 *                    ↓
 *              CANCELED (rejected/canceled)
 * </pre>
 *
 * <h2>Business rules:</h2>
 * <ul>
 *     <li>User: event date at least 2 hours from now</li>
 *     <li>Admin: publication at least 1 hour before the event</li>
 *     <li>Only unpublished events can be modified</li>
 *     <li>Only unpublished events can be rejected</li>
 * </ul>
 *
 * @author ExploreWithMe Team
 * @version 1.0
 * @see EventService
 * @see ru.practicum.client.StatsClient
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    /** Application name for stats */
    private static final String APP_NAME = "ewm-main-service";

    /** Minimum time before the event for a user (hours) */
    private static final int HOURS_BEFORE_EVENT_USER = 2;

    /** Minimum time before the event for admin publication (hours) */
    private static final int HOURS_BEFORE_EVENT_ADMIN = 1;

    private static final String DEFAULT_REJECT_REASON = "Отклонено администратором";
    private static final String SORT_VIEWS = "VIEWS";
    private static final String SORT_RATING = "RATING";
    private static final double KM_PER_LATITUDE_DEGREE = 111.32d;
    private static final double EARTH_RADIUS_KM = 6371.0088d;

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;
    private final EventModerationLogRepository eventModerationLogRepository;
    private final EventModerationLogMapper eventModerationLogMapper;
    private final EventRatingRepository eventRatingRepository;
    private final ManagedLocationRepository managedLocationRepository;

    // Private API — operations for authorized users

    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        log.info("Получение событий пользователя userId={}, from={}, size={}", userId, from, size);
        PaginationValidator.validatePagination(from, size);
        validateUserExists(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable).getContent();
        enrichEventsWithViews(events);

        return eventMapper.toEventShortDtoList(events);
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        log.info("Создание события пользователем userId={}", userId);

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + userId));

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория не найдена: id=" + newEventDto.getCategory()));

        validateEventDate(newEventDto.getEventDate(), HOURS_BEFORE_EVENT_USER);

        Event event = eventMapper.toEvent(newEventDto);
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setLocation(eventMapper.toLocation(newEventDto.getLocation()));

        Event savedEvent = eventRepository.save(event);
        log.info("Создано событие: id={}", savedEvent.getId());

        return eventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        log.info("Получение события eventId={} пользователя userId={}", eventId, userId);
        validateUserExists(userId);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + eventId));
        enrichEventWithViews(event);

        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        log.info("Обновление события eventId={} пользователем userId={}", eventId, userId);
        validateUserExists(userId);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + eventId));

        // Changes are allowed only for canceled or pending moderation events
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Нельзя изменить опубликованное событие");
        }

        if (updateRequest.getEventDate() != null) {
            validateEventDate(updateRequest.getEventDate(), HOURS_BEFORE_EVENT_USER);
        }

        if (updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена: id=" + updateRequest.getCategory()));
            event.setCategory(category);
        }

        eventMapper.updateEventFromUserRequest(updateRequest, event);

        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction()) {
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
            }
        }

        Event updatedEvent = eventRepository.save(event);
        log.info("Событие обновлено: id={}", updatedEvent.getId());

        return eventMapper.toEventFullDto(updatedEvent);
    }

    // Admin API — operations for administrators

    @Override
    public List<EventFullDto> searchEventsForAdmin(
            List<Long> users,
            List<EventState> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size) {
        log.info("Админ-поиск событий: users={}, states={}, categories={}", users, states, categories);
        PaginationValidator.validatePagination(from, size);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());

        List<Event> events = eventRepository.findEventsForAdmin(
                users, states, categories, rangeStart, rangeEnd, pageable).getContent();
        enrichEventsWithViews(events);

        return eventMapper.toEventFullDtoList(events);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        log.info("Обновление события eventId={} администратором", eventId);


        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + eventId));

        if (updateRequest.getEventDate() != null) {
            validateEventDate(updateRequest.getEventDate(), HOURS_BEFORE_EVENT_ADMIN);
        }

        if (updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена: id=" + updateRequest.getCategory()));
            event.setCategory(category);
        }

        eventMapper.updateEventFromAdminRequest(updateRequest, event);
        if (updateRequest.getModerationNote() != null) {
            event.setModerationNote(normalizeNote(updateRequest.getModerationNote()));
        }

        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction()) {
                case PUBLISH_EVENT -> {
                    if (event.getState() != EventState.PENDING) {
                        throw new ConflictException("Опубликовать можно только событие в статусе ожидания");
                    }
                    // Enforce the minimum time before publication
                    if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(HOURS_BEFORE_EVENT_ADMIN))) {
                        throw new ConflictException("Дата начала события должна быть не ранее чем за час от публикации");
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    String note = normalizeNote(updateRequest.getModerationNote());
                    event.setModerationNote(note);
                    saveModerationLog(event, EventModerationAction.PUBLISH, note);
                }
                case REJECT_EVENT -> {
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Нельзя отклонить опубликованное событие");
                    }
                    String note = hasText(updateRequest.getModerationNote())
                            ? updateRequest.getModerationNote().trim()
                            : DEFAULT_REJECT_REASON;
                    event.setState(EventState.CANCELED);
                    event.setModerationNote(note);
                    saveModerationLog(event, EventModerationAction.REJECT, note);
                }
            }
        }

        Event updatedEvent = eventRepository.save(event);
        log.info("Событие обновлено администратором: id={}", updatedEvent.getId());

        return eventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    public List<EventModerationLogDto> getEventModerationHistory(Long eventId, int from, int size) {
        log.info("Получение истории модерации события eventId={}", eventId);
        PaginationValidator.validatePagination(from, size);

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие не найдено: id=" + eventId);
        }

        Pageable pageable = PageRequest.of(
                from / size,
                size,
                Sort.by("actedOn").descending().and(Sort.by("id").descending())
        );

        List<EventModerationLog> history = eventModerationLogRepository.findAllByEventId(eventId, pageable)
                .getContent();
        return eventModerationLogMapper.toDtoList(history);
    }

    // Public API — public operations

    @Override
    public List<EventShortDto> searchPublicEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            int from,
            int size,
            HttpServletRequest request) {
        log.info("Публичный поиск событий: text={}, categories={}, paid={}", text, categories, paid);
        PaginationValidator.validatePagination(from, size);

        // If the range is not specified, use now as the start
        LocalDateTime start = rangeStart != null ? rangeStart : LocalDateTime.now();
        LocalDateTime end = rangeEnd;

        // Validate the date range
        if (end != null && start.isAfter(end)) {
            throw new ValidationException("Дата начала не может быть после даты окончания");
        }

        boolean sortByViews = isSortByViews(sort);
        boolean sortByRating = isSortByRating(sort);
        Pageable pageable = (sortByViews || sortByRating)
                ? Pageable.unpaged()
                : PageRequest.of(from / size, size, Sort.by("eventDate").ascending());

        List<Event> events = eventRepository.findPublicEvents(
                text, categories, paid, start, end,
                onlyAvailable != null && onlyAvailable, pageable).getContent();

        // Record the request in stats
        saveHit(request);

        // Fetch view statistics
        enrichEventsWithViews(events);

        // Apply in-memory sorting/pagination for sorts that depend on external data.
        if (sortByViews || sortByRating) {
            events = sortEvents(events, sort, Comparator.comparing(Event::getEventDate));
            events = applyManualPagination(events, from, size);
        }

        return eventMapper.toEventShortDtoList(events);
    }

    @Override
    public EventFullDto getPublishedEventById(Long eventId, HttpServletRequest request) {
        log.info("Получение опубликованного события: id={}", eventId);

        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + eventId));

        // Record the request in stats
        saveHit(request);

        // Update event views from stats
        enrichEventWithViews(event);

        return eventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + eventId));
        enrichEventWithViews(event);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getPublishedEventsByInitiators(List<Long> initiatorIds,
                                                              String sort,
                                                              int from,
                                                              int size) {
        PaginationValidator.validatePagination(from, size);
        if (initiatorIds == null || initiatorIds.isEmpty()) {
            return List.of();
        }

        List<Long> uniqueInitiatorIds = initiatorIds.stream()
                .distinct()
                .toList();
        boolean sortByViews = isSortByViews(sort);
        boolean sortByRating = isSortByRating(sort);

        Pageable pageable = (sortByViews || sortByRating)
                ? Pageable.unpaged()
                : PageRequest.of(
                        from / size,
                        size,
                        Sort.by("eventDate").descending().and(Sort.by("id").descending())
                );

        List<Event> events = eventRepository.findAllByInitiatorIdInAndState(
                        uniqueInitiatorIds,
                        EventState.PUBLISHED,
                        pageable
                )
                .getContent();
        enrichEventsWithViews(events);

        if (sortByViews || sortByRating) {
            events = sortEvents(events, sort, Comparator.comparing(Event::getEventDate).reversed());
            events = applyManualPagination(events, from, size);
        }

        return eventMapper.toEventShortDtoList(events);
    }

    @Override
    public List<EventShortDto> searchPublicEventsByLocation(Long locationId,
                                                            Double radiusKm,
                                                            String sort,
                                                            int from,
                                                            int size,
                                                            HttpServletRequest request) {
        PaginationValidator.validatePagination(from, size);

        ManagedLocation location = managedLocationRepository.findByIdAndActiveTrue(locationId)
                .orElseThrow(() -> new NotFoundException("Активная локация не найдена: id=" + locationId));

        double effectiveRadiusKm = radiusKm != null ? radiusKm : location.getRadiusKm();
        if (effectiveRadiusKm <= 0) {
            throw new ValidationException("Радиус поиска должен быть больше 0");
        }

        double centerLat = location.getLat();
        double centerLon = location.getLon();
        double latDelta = effectiveRadiusKm / KM_PER_LATITUDE_DEGREE;
        double cosLatitude = Math.cos(Math.toRadians(centerLat));
        double safeCos = Math.max(Math.abs(cosLatitude), 0.01d);
        double lonDelta = effectiveRadiusKm / (KM_PER_LATITUDE_DEGREE * safeCos);

        List<Event> candidates = eventRepository.findPublishedEventsInBoundingBox(
                        (float) (centerLat - latDelta),
                        (float) (centerLat + latDelta),
                        (float) (centerLon - lonDelta),
                        (float) (centerLon + lonDelta),
                        Pageable.unpaged())
                .getContent();

        List<Event> nearbyEvents = new ArrayList<>(candidates.stream()
                .filter(event -> event.getLocation() != null
                        && event.getLocation().getLat() != null
                        && event.getLocation().getLon() != null
                        && calculateDistanceKm(
                        centerLat,
                        centerLon,
                        event.getLocation().getLat(),
                        event.getLocation().getLon()
                ) <= effectiveRadiusKm)
                .toList());

        saveHit(request);
        enrichEventsWithViews(nearbyEvents);

        List<Event> sorted = sortEvents(nearbyEvents, sort, Comparator.comparing(Event::getEventDate));
        List<Event> paged = applyManualPagination(sorted, from, size);
        return eventMapper.toEventShortDtoList(paged);
    }

    // Private methods — helper operations

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден: id=" + userId);
        }
    }

    private void validateEventDate(LocalDateTime eventDate, int hoursBeforeEvent) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(hoursBeforeEvent))) {
            throw new ValidationException(
                    String.format("Дата события должна быть не ранее чем через %d часа от текущего момента",
                            hoursBeforeEvent));
        }
    }


    private void saveHit(HttpServletRequest request) {
        try {
            EndpointHitDto hit = EndpointHitDto.builder()
                    .app(APP_NAME)
                    .uri(request.getRequestURI())
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now())
                    .build();
            statsClient.hit(hit);
            log.debug("Статистика сохранена: uri={}, ip={}", hit.getUri(), hit.getIp());
        } catch (Exception e) {
            log.warn("Ошибка при сохранении статистики: {}", e.getMessage());
        }
    }

    private Map<Long, Long> getViewsForEvents(List<Event> events) {
        if (events.isEmpty()) {
            return Map.of();
        }

        try {
            List<String> uris = events.stream()
                    .map(e -> "/events/" + e.getId())
                    .collect(Collectors.toList());

            LocalDateTime start = events.stream()
                    .map(Event::getCreatedOn)
                    .min(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now().minusYears(1));

            StatsRequestDto requestDto = new StatsRequestDto();
            requestDto.setStart(start);
            requestDto.setEnd(LocalDateTime.now());
            requestDto.setUris(uris);
            requestDto.setUnique(true);

            List<ViewStatsDto> stats = statsClient.getStats(requestDto);

            return stats.stream()
                    .collect(Collectors.toMap(
                            s -> extractEventIdFromUri(s.getUri()),
                            ViewStatsDto::getHits,
                            (a, b) -> a));
        } catch (Exception e) {
            log.warn("Ошибка при получении статистики: {}", e.getMessage());
            return Map.of();
        }
    }

    private Long extractEventIdFromUri(String uri) {
        String[] parts = uri.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }

    private void enrichEventsWithViews(List<Event> events) {
        if (events.isEmpty()) {
            return;
        }
        Map<Long, Long> viewsMap = getViewsForEvents(events);
        events.forEach(event -> event.setViews(viewsMap.getOrDefault(event.getId(), 0L)));
    }

    private void enrichEventWithViews(Event event) {
        enrichEventsWithViews(List.of(event));
    }

    private List<Event> sortEvents(List<Event> events, String sort, Comparator<Event> defaultComparator) {
        if (events.isEmpty()) {
            return events;
        }

        if (isSortByViews(sort)) {
            return events.stream()
                    .sorted(Comparator.comparing(
                                    Event::getViews,
                                    Comparator.nullsLast(Comparator.reverseOrder())
                            )
                            .thenComparing(Event::getEventDate, Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();
        }

        if (isSortByRating(sort)) {
            Map<Long, Long> scoreMap = getRatingScoresForEvents(events);
            return events.stream()
                    .sorted(Comparator
                            .comparing(
                                    (Event event) -> scoreMap.getOrDefault(event.getId(), 0L),
                                    Comparator.reverseOrder()
                            )
                            .thenComparing(
                                    Event::getViews,
                                    Comparator.nullsLast(Comparator.reverseOrder())
                            )
                            .thenComparing(
                                    Event::getEventDate,
                                    Comparator.nullsLast(Comparator.naturalOrder())
                            ))
                    .toList();
        }

        return events.stream()
                .sorted(defaultComparator)
                .toList();
    }

    private Map<Long, Long> getRatingScoresForEvents(List<Event> events) {
        if (events.isEmpty()) {
            return Map.of();
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();

        return eventRatingRepository.findScoresByEventIds(eventIds, VoteType.LIKE, VoteType.DISLIKE)
                .stream()
                .collect(Collectors.toMap(
                        EventRatingRepository.EventScoreProjection::getEventId,
                        score -> score.getScore() == null ? 0L : score.getScore().longValue()
                ));
    }

    private boolean isSortByViews(String sort) {
        return SORT_VIEWS.equalsIgnoreCase(sort);
    }

    private boolean isSortByRating(String sort) {
        return SORT_RATING.equalsIgnoreCase(sort);
    }

    private List<Event> applyManualPagination(List<Event> events, int from, int size) {
        int startIdx = Math.min(from, events.size());
        int endIdx = Math.min(startIdx + size, events.size());
        return events.subList(startIdx, endIdx);
    }

    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    private void saveModerationLog(Event event, EventModerationAction action, String note) {
        EventModerationLog logEntry = EventModerationLog.builder()
                .event(event)
                .action(action)
                .note(note)
                .actedOn(LocalDateTime.now())
                .build();
        eventModerationLogRepository.save(logEntry);
    }

    private String normalizeNote(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
