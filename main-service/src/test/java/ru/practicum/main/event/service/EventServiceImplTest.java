package ru.practicum.main.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.client.StatsClient;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link EventServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EventService Unit Tests")
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private StatsClient statsClient;

    @InjectMocks
    private EventServiceImpl eventService;

    private User testUser;
    private Category testCategory;
    private Event testEvent;
    private Location testLocation;
    private EventFullDto testEventFullDto;
    private EventShortDto testEventShortDto;
    private NewEventDto testNewEventDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@test.com");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");

        testLocation = new Location();
        testLocation.setLat(55.75f);
        testLocation.setLon(37.62f);

        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("Test Event");
        testEvent.setAnnotation("Test annotation for event");
        testEvent.setDescription("Test description");
        testEvent.setCategory(testCategory);
        testEvent.setInitiator(testUser);
        testEvent.setLocation(testLocation);
        testEvent.setEventDate(LocalDateTime.now().plusDays(7));
        testEvent.setCreatedOn(LocalDateTime.now());
        testEvent.setState(EventState.PENDING);
        testEvent.setPaid(false);
        testEvent.setParticipantLimit(100);
        testEvent.setRequestModeration(true);
        testEvent.setConfirmedRequests(0L);
        testEvent.setViews(0L);

        testEventFullDto = EventFullDto.builder()
                .id(1L)
                .title("Test Event")
                .annotation("Test annotation for event")
                .description("Test description")
                .state(EventState.PENDING)
                .build();

        testEventShortDto = EventShortDto.builder()
                .id(1L)
                .title("Test Event")
                .annotation("Test annotation for event")
                .build();

        testNewEventDto = NewEventDto.builder()
                .title("New Event")
                .annotation("New event annotation with 20 chars minimum")
                .description("New event description with 20 chars minimum")
                .category(1L)
                .eventDate(LocalDateTime.now().plusDays(7))
                .location(new LocationDto(55.75f, 37.62f))
                .paid(false)
                .participantLimit(100)
                .requestModeration(true)
                .build();
    }

    // Private API tests

    @Nested
    @DisplayName("Private API: getUserEvents")
    class GetUserEventsTests {

        @Test
        @DisplayName("Должен вернуть список событий пользователя")
        void getUserEvents_ReturnsEventList() {
            // Setup
            when(userRepository.existsById(1L)).thenReturn(true);
            Page<Event> eventPage = new PageImpl<>(List.of(testEvent));
            when(eventRepository.findAllByInitiatorId(anyLong(), any(Pageable.class)))
                    .thenReturn(eventPage);
            when(eventMapper.toEventShortDtoList(any())).thenReturn(List.of(testEventShortDto));

            // Action
            List<EventShortDto> result = eventService.getUserEvents(1L, 0, 10);

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Test Event");
            verify(eventRepository).findAllByInitiatorId(anyLong(), any(Pageable.class));
        }

        @Test
        @DisplayName("Должен выбросить NotFoundException если пользователь не найден")
        void getUserEvents_UserNotFound_ThrowsException() {
            // Setup
            when(userRepository.existsById(999L)).thenReturn(false);

            // Action and assert
            assertThatThrownBy(() -> eventService.getUserEvents(999L, 0, 10))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Пользователь не найден");
        }
    }

    @Nested
    @DisplayName("Private API: createEvent")
    class CreateEventTests {

        @Test
        @DisplayName("Должен создать событие успешно")
        void createEvent_Success() {
            // Setup
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
            when(eventMapper.toEvent(any(NewEventDto.class))).thenReturn(testEvent);
            when(eventMapper.toLocation(any(LocationDto.class))).thenReturn(testLocation);
            when(eventRepository.save(any(Event.class))).thenReturn(testEvent);
            when(eventMapper.toEventFullDto(any(Event.class))).thenReturn(testEventFullDto);

            // Action
            EventFullDto result = eventService.createEvent(1L, testNewEventDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Test Event");
            verify(eventRepository).save(any(Event.class));
        }

        @Test
        @DisplayName("Должен выбросить NotFoundException если пользователь не найден")
        void createEvent_UserNotFound_ThrowsException() {
            // Setup
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Action and assert
            assertThatThrownBy(() -> eventService.createEvent(999L, testNewEventDto))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Пользователь не найден");
        }

        @Test
        @DisplayName("Должен выбросить NotFoundException если категория не найдена")
        void createEvent_CategoryNotFound_ThrowsException() {
            // Setup
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            // Action and assert
            assertThatThrownBy(() -> eventService.createEvent(1L, testNewEventDto))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Категория не найдена");
        }

        @Test
        @DisplayName("Должен выбросить ValidationException если дата события слишком близко")
        void createEvent_EventDateTooSoon_ThrowsException() {
            // Setup
            testNewEventDto.setEventDate(LocalDateTime.now().plusHours(1));
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

            // Action and assert
            assertThatThrownBy(() -> eventService.createEvent(1L, testNewEventDto))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Дата события должна быть не ранее");
        }
    }

    @Nested
    @DisplayName("Private API: getUserEventById")
    class GetUserEventByIdTests {

        @Test
        @DisplayName("Должен вернуть событие пользователя по ID")
        void getUserEventById_Success() {
            // Setup
            when(userRepository.existsById(1L)).thenReturn(true);
            when(eventRepository.findByIdAndInitiatorId(1L, 1L)).thenReturn(Optional.of(testEvent));
            when(eventMapper.toEventFullDto(testEvent)).thenReturn(testEventFullDto);

            // Action
            EventFullDto result = eventService.getUserEventById(1L, 1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Должен выбросить NotFoundException если событие не найдено")
        void getUserEventById_EventNotFound_ThrowsException() {
            // Setup
            when(userRepository.existsById(1L)).thenReturn(true);
            when(eventRepository.findByIdAndInitiatorId(999L, 1L)).thenReturn(Optional.empty());

            // Action and assert
            assertThatThrownBy(() -> eventService.getUserEventById(1L, 999L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Событие не найдено");
        }
    }

    @Nested
    @DisplayName("Private API: updateEventByUser")
    class UpdateEventByUserTests {

        @Test
        @DisplayName("Должен обновить событие успешно")
        void updateEventByUser_Success() {
            // Setup
            UpdateEventUserRequest updateRequest = new UpdateEventUserRequest();
            updateRequest.setTitle("Updated Title");
            updateRequest.setStateAction(UpdateEventUserRequest.StateAction.SEND_TO_REVIEW);

            when(userRepository.existsById(1L)).thenReturn(true);
            when(eventRepository.findByIdAndInitiatorId(1L, 1L)).thenReturn(Optional.of(testEvent));
            when(eventRepository.save(any(Event.class))).thenReturn(testEvent);
            when(eventMapper.toEventFullDto(any(Event.class))).thenReturn(testEventFullDto);

            // Action
            EventFullDto result = eventService.updateEventByUser(1L, 1L, updateRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(eventRepository).save(any(Event.class));
        }

        @Test
        @DisplayName("Должен выбросить ConflictException если событие уже опубликовано")
        void updateEventByUser_EventPublished_ThrowsException() {
            // Setup
            testEvent.setState(EventState.PUBLISHED);
            UpdateEventUserRequest updateRequest = new UpdateEventUserRequest();
            updateRequest.setTitle("Updated Title");

            when(userRepository.existsById(1L)).thenReturn(true);
            when(eventRepository.findByIdAndInitiatorId(1L, 1L)).thenReturn(Optional.of(testEvent));

                // Action and assert
            assertThatThrownBy(() -> eventService.updateEventByUser(1L, 1L, updateRequest))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("опубликованное событие");
        }

        @Test
        @DisplayName("Должен отменить событие при CANCEL_REVIEW")
        void updateEventByUser_CancelReview_Success() {
            // Setup
            UpdateEventUserRequest updateRequest = new UpdateEventUserRequest();
            updateRequest.setStateAction(UpdateEventUserRequest.StateAction.CANCEL_REVIEW);

            when(userRepository.existsById(1L)).thenReturn(true);
            when(eventRepository.findByIdAndInitiatorId(1L, 1L)).thenReturn(Optional.of(testEvent));
            when(eventRepository.save(any(Event.class))).thenAnswer(inv -> {
                Event e = inv.getArgument(0);
                assertThat(e.getState()).isEqualTo(EventState.CANCELED);
                return e;
            });
            when(eventMapper.toEventFullDto(any(Event.class))).thenReturn(testEventFullDto);

            // Action
            eventService.updateEventByUser(1L, 1L, updateRequest);

            // Assert
            verify(eventRepository).save(any(Event.class));
        }
    }

    // Admin API tests

    @Nested
    @DisplayName("Admin API: searchEventsForAdmin")
    class SearchEventsForAdminTests {

        @Test
        @DisplayName("Должен вернуть список событий по фильтрам")
        void searchEventsForAdmin_ReturnsFilteredEvents() {
            // Setup
            Page<Event> eventPage = new PageImpl<>(List.of(testEvent));
            when(eventRepository.findEventsForAdmin(any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(eventPage);
            when(eventMapper.toEventFullDtoList(any())).thenReturn(List.of(testEventFullDto));

            // Action
            List<EventFullDto> result = eventService.searchEventsForAdmin(
                    List.of(1L), List.of(EventState.PENDING), List.of(1L),
                    LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(7),
                    0, 10);

            // Assert
            assertThat(result).hasSize(1);
            verify(eventRepository).findEventsForAdmin(any(), any(), any(), any(), any(), any(Pageable.class));
        }

        @Test
        @DisplayName("Должен вернуть пустой список если событий нет")
        void searchEventsForAdmin_NoEvents_ReturnsEmptyList() {
            // Setup
            Page<Event> emptyPage = new PageImpl<>(List.of());
            when(eventRepository.findEventsForAdmin(any(), any(), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(emptyPage);
            when(eventMapper.toEventFullDtoList(any())).thenReturn(List.of());

            // Action
            List<EventFullDto> result = eventService.searchEventsForAdmin(
                    null, null, null, null, null, 0, 10);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Admin API: updateEventByAdmin")
    class UpdateEventByAdminTests {

        @Test
        @DisplayName("Должен опубликовать событие")
        void updateEventByAdmin_PublishEvent_Success() {
            // Setup
            testEvent.setEventDate(LocalDateTime.now().plusDays(1));
            UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
            updateRequest.setStateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);

            when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
            when(eventRepository.save(any(Event.class))).thenAnswer(inv -> {
                Event e = inv.getArgument(0);
                assertThat(e.getState()).isEqualTo(EventState.PUBLISHED);
                assertThat(e.getPublishedOn()).isNotNull();
                return e;
            });
            when(eventMapper.toEventFullDto(any(Event.class))).thenReturn(testEventFullDto);

            // Action
            eventService.updateEventByAdmin(1L, updateRequest);

            // Assert
            verify(eventRepository).save(any(Event.class));
        }

        @Test
        @DisplayName("Должен отклонить событие")
        void updateEventByAdmin_RejectEvent_Success() {
            // Setup
            UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
            updateRequest.setStateAction(UpdateEventAdminRequest.StateAction.REJECT_EVENT);

            when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
            when(eventRepository.save(any(Event.class))).thenAnswer(inv -> {
                Event e = inv.getArgument(0);
                assertThat(e.getState()).isEqualTo(EventState.CANCELED);
                return e;
            });
            when(eventMapper.toEventFullDto(any(Event.class))).thenReturn(testEventFullDto);

            // Action
            eventService.updateEventByAdmin(1L, updateRequest);

            // Assert
            verify(eventRepository).save(any(Event.class));
        }

        @Test
        @DisplayName("Должен выбросить ConflictException при публикации не PENDING события")
        void updateEventByAdmin_PublishNotPending_ThrowsException() {
            // Setup
            testEvent.setState(EventState.CANCELED);
            UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
            updateRequest.setStateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);

            when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

                // Action and assert
            assertThatThrownBy(() -> eventService.updateEventByAdmin(1L, updateRequest))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("ожидания");
        }

        @Test
        @DisplayName("Должен выбросить ConflictException при отклонении опубликованного события")
        void updateEventByAdmin_RejectPublished_ThrowsException() {
            // Setup
            testEvent.setState(EventState.PUBLISHED);
            UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
            updateRequest.setStateAction(UpdateEventAdminRequest.StateAction.REJECT_EVENT);

            when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

                // Action and assert
            assertThatThrownBy(() -> eventService.updateEventByAdmin(1L, updateRequest))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("опубликованное");
        }

        @Test
        @DisplayName("Должен выбросить NotFoundException если событие не найдено")
        void updateEventByAdmin_EventNotFound_ThrowsException() {
            // Setup
            UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
            when(eventRepository.findById(999L)).thenReturn(Optional.empty());

            // Action and assert
            assertThatThrownBy(() -> eventService.updateEventByAdmin(999L, updateRequest))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Событие не найдено");
        }
    }

    // Public API tests

    @Nested
    @DisplayName("Public API: getPublishedEventById")
    class GetPublishedEventByIdTests {

        @Test
        @DisplayName("Должен вернуть опубликованное событие")
        void getPublishedEventById_Success() {
            // Setup
            testEvent.setState(EventState.PUBLISHED);
            when(eventRepository.findByIdAndState(1L, EventState.PUBLISHED))
                    .thenReturn(Optional.of(testEvent));
            when(eventMapper.toEventFullDto(any(Event.class))).thenReturn(testEventFullDto);

            // Action
            EventFullDto result = eventService.getPublishedEventById(1L, mock(jakarta.servlet.http.HttpServletRequest.class));

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Должен выбросить NotFoundException если событие не опубликовано")
        void getPublishedEventById_NotPublished_ThrowsException() {
            // Setup
            when(eventRepository.findByIdAndState(1L, EventState.PUBLISHED))
                    .thenReturn(Optional.empty());

            // Action and assert
            assertThatThrownBy(() -> eventService.getPublishedEventById(1L,
                    mock(jakarta.servlet.http.HttpServletRequest.class)))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Событие не найдено");
        }
    }

    @Nested
    @DisplayName("Public API: getEventById")
    class GetEventByIdTests {

        @Test
        @DisplayName("Должен вернуть событие по ID")
        void getEventById_Success() {
            // Setup
            when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
            when(eventMapper.toEventFullDto(testEvent)).thenReturn(testEventFullDto);

            // Action
            EventFullDto result = eventService.getEventById(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Должен выбросить NotFoundException если событие не найдено")
        void getEventById_NotFound_ThrowsException() {
            // Setup
            when(eventRepository.findById(999L)).thenReturn(Optional.empty());

            // Action and assert
            assertThatThrownBy(() -> eventService.getEventById(999L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Событие не найдено");
        }
    }
}
