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
import ru.practicum.dto.StatsResponseDto;
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
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
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

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;

    // Private API — operations for authorized users

    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        log.info("Получение событий пользователя userId={}, from={}, size={}", userId, from, size);
        validateUserExists(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable).getContent();

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

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());

        List<Event> events = eventRepository.findEventsForAdmin(
                users, states, categories, rangeStart, rangeEnd, pageable).getContent();

        return eventMapper.toEventFullDtoList(events);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        log.info("Обновление события eventId={} администратором", eventId);

        validateAdminUpdateRequest(updateRequest);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + eventId));

        if (updateRequest.getEventDate() != null) {
            validateEventDate(updateRequest.getEventDate(), HOURS_BEFORE_EVENT_ADMIN);
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
                }
                case REJECT_EVENT -> {
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Нельзя отклонить опубликованное событие");
                    }
                    event.setState(EventState.CANCELED);
                }
            }
        }

        if (updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена: id=" + updateRequest.getCategory()));
            event.setCategory(category);
        }

        eventMapper.updateEventFromAdminRequest(updateRequest, event);

        Event updatedEvent = eventRepository.save(event);
        log.info("Событие обновлено администратором: id={}", updatedEvent.getId());

        return eventMapper.toEventFullDto(updatedEvent);
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

        // If the range is not specified, use now as the start
        LocalDateTime start = rangeStart != null ? rangeStart : LocalDateTime.now();
        LocalDateTime end = rangeEnd;

        // Validate the date range
        if (end != null && start.isAfter(end)) {
            throw new ValidationException("Дата начала не может быть после даты окончания");
        }

        boolean sortByViews = "VIEWS".equalsIgnoreCase(sort);
        Pageable pageable = sortByViews
                ? Pageable.unpaged()
                : PageRequest.of(from / size, size, Sort.by("eventDate").ascending());

        List<Event> events = eventRepository.findPublicEvents(
                text, categories, paid, start, end,
                onlyAvailable != null && onlyAvailable, pageable).getContent();

        // Record the request in stats
        saveHit(request);

        // Fetch view statistics
        Map<Long, Long> viewsMap = getViewsForEvents(events);
        events.forEach(e -> e.setViews(viewsMap.getOrDefault(e.getId(), 0L)));

        // Apply sorting to the result and paginate when sorting by views
        if (sortByViews) {
            events = events.stream()
                    .sorted(Comparator.comparing(Event::getViews).reversed())
                    .collect(Collectors.toList());

            int startIdx = Math.min(from, events.size());
            int endIdx = Math.min(startIdx + size, events.size());
            events = events.subList(startIdx, endIdx);
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
        Map<Long, Long> viewsMap = getViewsForEvents(List.of(event));
        event.setViews(viewsMap.getOrDefault(eventId, 0L));

        return eventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + eventId));
        return eventMapper.toEventFullDto(event);
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

    private void validateAdminUpdateRequest(UpdateEventAdminRequest updateRequest) {
        if (updateRequest == null) {
            return;
        }

        String annotation = updateRequest.getAnnotation();
        if (annotation != null && (annotation.length() < 20 || annotation.length() > 2000)) {
            throw new ValidationException("Аннотация должна быть от 20 до 2000 символов");
        }

        String description = updateRequest.getDescription();
        if (description != null && (description.length() < 20 || description.length() > 7000)) {
            throw new ValidationException("Описание должно быть от 20 до 7000 символов");
        }

        String title = updateRequest.getTitle();
        if (title != null && (title.length() < 3 || title.length() > 120)) {
            throw new ValidationException("Заголовок должен быть от 3 до 120 символов");
        }

        Integer participantLimit = updateRequest.getParticipantLimit();
        if (participantLimit != null && participantLimit < 0) {
            throw new ValidationException("Лимит участников не может быть отрицательным");
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

            List<StatsResponseDto> stats = statsClient.getStats(requestDto);

            return stats.stream()
                    .collect(Collectors.toMap(
                            s -> extractEventIdFromUri(s.getUri()),
                            StatsResponseDto::getHits,
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
}
