package ru.practicum.main.event.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link EventMapper}.
 */
@DisplayName("EventMapper Tests")
class EventMapperTest {

    private EventMapper eventMapper;

    private User user;
    private Category category;
    private Location location;
    private Event event;

    @BeforeEach
    void setUp() {
        eventMapper = Mappers.getMapper(EventMapper.class);

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@test.com");

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        location = new Location();
        location.setLat(55.75f);
        location.setLon(37.62f);

        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setAnnotation("Test annotation for event");
        event.setDescription("Full description of the test event");
        event.setCategory(category);
        event.setInitiator(user);
        event.setLocation(location);
        event.setEventDate(LocalDateTime.now().plusDays(7));
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        event.setPaid(true);
        event.setParticipantLimit(100);
        event.setRequestModeration(true);
        event.setConfirmedRequests(10L);
        event.setViews(50L);
    }

    @Test
    @DisplayName("toEventFullDto: должен корректно маппить Event в EventFullDto")
    void toEventFullDto_MapsCorrectly() {
        // Action
        EventFullDto result = eventMapper.toEventFullDto(event);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Event");
        assertThat(result.getAnnotation()).isEqualTo("Test annotation for event");
        assertThat(result.getDescription()).isEqualTo("Full description of the test event");
        assertThat(result.getState()).isEqualTo(EventState.PUBLISHED);
        assertThat(result.getPaid()).isTrue();
        assertThat(result.getParticipantLimit()).isEqualTo(100);
        assertThat(result.getRequestModeration()).isTrue();
        assertThat(result.getConfirmedRequests()).isEqualTo(10L);
        assertThat(result.getViews()).isEqualTo(50L);

        // Assert nested objects
        assertThat(result.getCategory()).isNotNull();
        assertThat(result.getCategory().getId()).isEqualTo(1L);
        assertThat(result.getCategory().getName()).isEqualTo("Test Category");

        assertThat(result.getInitiator()).isNotNull();
        assertThat(result.getInitiator().getId()).isEqualTo(1L);
        assertThat(result.getInitiator().getName()).isEqualTo("Test User");

        assertThat(result.getLocation()).isNotNull();
        assertThat(result.getLocation().getLat()).isEqualTo(55.75f);
        assertThat(result.getLocation().getLon()).isEqualTo(37.62f);
    }

    @Test
    @DisplayName("toEventShortDto: должен корректно маппить Event в EventShortDto")
    void toEventShortDto_MapsCorrectly() {
        // Action
        EventShortDto result = eventMapper.toEventShortDto(event);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Event");
        assertThat(result.getAnnotation()).isEqualTo("Test annotation for event");
        assertThat(result.getPaid()).isTrue();
        assertThat(result.getConfirmedRequests()).isEqualTo(10L);
        assertThat(result.getViews()).isEqualTo(50L);

        // Assert nested objects
        assertThat(result.getCategory()).isNotNull();
        assertThat(result.getCategory().getId()).isEqualTo(1L);

        assertThat(result.getInitiator()).isNotNull();
        assertThat(result.getInitiator().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("toEventShortDtoList: должен маппить список событий")
    void toEventShortDtoList_MapsCorrectly() {
        // Setup
        Event event2 = new Event();
        event2.setId(2L);
        event2.setTitle("Second Event");
        event2.setAnnotation("Second annotation");
        event2.setCategory(category);
        event2.setInitiator(user);
        event2.setEventDate(LocalDateTime.now().plusDays(14));
        event2.setPaid(false);
        event2.setConfirmedRequests(5L);
        event2.setViews(25L);

        List<Event> events = List.of(event, event2);

        // Action
        List<EventShortDto> result = eventMapper.toEventShortDtoList(events);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("toEventFullDtoList: должен маппить список в полные DTO")
    void toEventFullDtoList_MapsCorrectly() {
        // Setup
        List<Event> events = List.of(event);

        // Action
        List<EventFullDto> result = eventMapper.toEventFullDtoList(events);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getDescription()).isEqualTo("Full description of the test event");
    }

    @Test
    @DisplayName("toEvent: должен маппить NewEventDto в Event")
    void toEvent_MapsCorrectly() {
        // Setup
        NewEventDto newEventDto = NewEventDto.builder()
                .title("New Event")
                .annotation("New event annotation text")
                .description("New event description text")
                .eventDate(LocalDateTime.now().plusDays(7))
                .location(new LocationDto(55.75f, 37.62f))
                .paid(true)
                .participantLimit(50)
                .requestModeration(false)
                .build();

        // Action
        Event result = eventMapper.toEvent(newEventDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("New Event");
        assertThat(result.getAnnotation()).isEqualTo("New event annotation text");
        assertThat(result.getDescription()).isEqualTo("New event description text");
        assertThat(result.getPaid()).isTrue();
        assertThat(result.getParticipantLimit()).isEqualTo(50);
        assertThat(result.getRequestModeration()).isFalse();
    }

    @Test
    @DisplayName("toLocation: должен маппить LocationDto в Location")
    void toLocation_MapsCorrectly() {
        // Setup
        LocationDto locationDto = new LocationDto(60.0f, 30.0f);

        // Action
        Location result = eventMapper.toLocation(locationDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getLat()).isEqualTo(60.0f);
        assertThat(result.getLon()).isEqualTo(30.0f);
    }

    @Test
    @DisplayName("updateEventFromUserRequest: должен обновить поля события")
    void updateEventFromUserRequest_UpdatesFields() {
        // Setup
        UpdateEventUserRequest updateRequest = new UpdateEventUserRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setAnnotation("Updated annotation text");
        updateRequest.setPaid(false);
        updateRequest.setParticipantLimit(200);

        // Action
        eventMapper.updateEventFromUserRequest(updateRequest, event);

        // Assert
        assertThat(event.getTitle()).isEqualTo("Updated Title");
        assertThat(event.getAnnotation()).isEqualTo("Updated annotation text");
        assertThat(event.getPaid()).isFalse();
        assertThat(event.getParticipantLimit()).isEqualTo(200);
        // Fields that were not updated should remain unchanged
        assertThat(event.getDescription()).isEqualTo("Full description of the test event");
    }

    @Test
    @DisplayName("updateEventFromUserRequest: null поля не должны обновлять событие")
    void updateEventFromUserRequest_NullFields_NotUpdated() {
        // Setup
        String originalTitle = event.getTitle();
        String originalAnnotation = event.getAnnotation();

        UpdateEventUserRequest updateRequest = new UpdateEventUserRequest();
        // All fields are null

        // Action
        eventMapper.updateEventFromUserRequest(updateRequest, event);

        // Assert
        assertThat(event.getTitle()).isEqualTo(originalTitle);
        assertThat(event.getAnnotation()).isEqualTo(originalAnnotation);
    }

    @Test
    @DisplayName("updateEventFromAdminRequest: должен обновить поля события")
    void updateEventFromAdminRequest_UpdatesFields() {
        // Setup
        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setTitle("Admin Updated Title");
        updateRequest.setDescription("Admin updated description");

        // Action
        eventMapper.updateEventFromAdminRequest(updateRequest, event);

        // Assert
        assertThat(event.getTitle()).isEqualTo("Admin Updated Title");
        assertThat(event.getDescription()).isEqualTo("Admin updated description");
    }

    @Test
    @DisplayName("toEventFullDto: null input возвращает null")
    void toEventFullDto_NullInput_ReturnsNull() {
        // Action
        EventFullDto result = eventMapper.toEventFullDto(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toEventShortDtoList: пустой список возвращает пустой список")
    void toEventShortDtoList_EmptyList_ReturnsEmptyList() {
        // Action
        List<EventShortDto> result = eventMapper.toEventShortDtoList(List.of());

        // Assert
        assertThat(result).isEmpty();
    }
}
