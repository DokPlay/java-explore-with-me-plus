package ru.practicum.main.event.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit тесты для модели Event.
 */
@DisplayName("Event Model Tests")
class EventTest {

    @Test
    @DisplayName("Должен создать событие через builder")
    void builder_CreatesEvent() {
        // Given
        User initiator = User.builder().id(1L).name("Test User").email("test@test.com").build();
        Category category = Category.builder().id(1L).name("Test Category").build();
        Location location = Location.builder().lat(55.75f).lon(37.62f).build();
        LocalDateTime eventDate = LocalDateTime.now().plusDays(7);

        // When
        Event event = Event.builder()
                .id(1L)
                .title("Test Event")
                .annotation("Test Annotation")
                .description("Test Description")
                .eventDate(eventDate)
                .initiator(initiator)
                .category(category)
                .location(location)
                .paid(true)
                .participantLimit(100)
                .requestModeration(true)
                .state(EventState.PENDING)
                .build();

        // Then
        assertThat(event.getId()).isEqualTo(1L);
        assertThat(event.getTitle()).isEqualTo("Test Event");
        assertThat(event.getAnnotation()).isEqualTo("Test Annotation");
        assertThat(event.getDescription()).isEqualTo("Test Description");
        assertThat(event.getEventDate()).isEqualTo(eventDate);
        assertThat(event.getInitiator()).isEqualTo(initiator);
        assertThat(event.getCategory()).isEqualTo(category);
        assertThat(event.getLocation()).isEqualTo(location);
        assertThat(event.getPaid()).isTrue();
        assertThat(event.getParticipantLimit()).isEqualTo(100);
        assertThat(event.getRequestModeration()).isTrue();
        assertThat(event.getState()).isEqualTo(EventState.PENDING);
    }

    @Test
    @DisplayName("Должен создать событие через no-args конструктор")
    void noArgsConstructor_CreatesEmptyEvent() {
        // When
        Event event = new Event();

        // Then
        assertThat(event.getId()).isNull();
        assertThat(event.getTitle()).isNull();
        assertThat(event.getState()).isNull();
    }

    @Test
    @DisplayName("Setters должны устанавливать значения")
    void setters_SetValues() {
        // Given
        Event event = new Event();

        // When
        event.setId(1L);
        event.setTitle("Setter Event");
        event.setState(EventState.PUBLISHED);
        event.setPaid(false);
        event.setParticipantLimit(50);

        // Then
        assertThat(event.getId()).isEqualTo(1L);
        assertThat(event.getTitle()).isEqualTo("Setter Event");
        assertThat(event.getState()).isEqualTo(EventState.PUBLISHED);
        assertThat(event.getPaid()).isFalse();
        assertThat(event.getParticipantLimit()).isEqualTo(50);
    }

    @Test
    @DisplayName("Должен корректно устанавливать даты")
    void dates_AreSetCorrectly() {
        // Given
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime eventDate = LocalDateTime.now().plusDays(7);
        LocalDateTime publishedOn = LocalDateTime.now().plusDays(1);

        // When
        Event event = Event.builder()
                .createdOn(createdOn)
                .eventDate(eventDate)
                .publishedOn(publishedOn)
                .build();

        // Then
        assertThat(event.getCreatedOn()).isEqualTo(createdOn);
        assertThat(event.getEventDate()).isEqualTo(eventDate);
        assertThat(event.getPublishedOn()).isEqualTo(publishedOn);
    }

    @Test
    @DisplayName("Должен корректно работать с confirmedRequests")
    void confirmedRequests_WorksCorrectly() {
        // Given
        Event event = new Event();

        // When
        event.setConfirmedRequests(25);

        // Then
        assertThat(event.getConfirmedRequests()).isEqualTo(25);
    }
}
