package ru.practicum.main.event.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit тесты для EventFullDto.
 */
@DisplayName("EventFullDto Tests")
class EventFullDtoTest {

    @Test
    @DisplayName("Должен создать DTO через builder")
    void builder_CreatesDto() {
        // Given
        LocalDateTime eventDate = LocalDateTime.now().plusDays(7);
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime publishedOn = LocalDateTime.now().plusDays(1);
        UserShortDto initiator = new UserShortDto(1L, "Test User");
        CategoryDto category = new CategoryDto(1L, "Test Category");
        LocationDto location = new LocationDto(55.75f, 37.62f);

        // When
        EventFullDto dto = EventFullDto.builder()
                .id(1L)
                .title("Test Event")
                .annotation("Test Annotation")
                .description("Test Description")
                .eventDate(eventDate)
                .createdOn(createdOn)
                .publishedOn(publishedOn)
                .initiator(initiator)
                .category(category)
                .location(location)
                .paid(true)
                .participantLimit(100)
                .requestModeration(true)
                .confirmedRequests(25)
                .views(1000L)
                .state(EventState.PUBLISHED)
                .build();

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Test Event");
        assertThat(dto.getAnnotation()).isEqualTo("Test Annotation");
        assertThat(dto.getDescription()).isEqualTo("Test Description");
        assertThat(dto.getEventDate()).isEqualTo(eventDate);
        assertThat(dto.getCreatedOn()).isEqualTo(createdOn);
        assertThat(dto.getPublishedOn()).isEqualTo(publishedOn);
        assertThat(dto.getInitiator()).isEqualTo(initiator);
        assertThat(dto.getCategory()).isEqualTo(category);
        assertThat(dto.getLocation()).isEqualTo(location);
        assertThat(dto.getPaid()).isTrue();
        assertThat(dto.getParticipantLimit()).isEqualTo(100);
        assertThat(dto.getRequestModeration()).isTrue();
        assertThat(dto.getConfirmedRequests()).isEqualTo(25);
        assertThat(dto.getViews()).isEqualTo(1000L);
        assertThat(dto.getState()).isEqualTo(EventState.PUBLISHED);
    }

    @Test
    @DisplayName("Должен создать пустой DTO")
    void noArgsConstructor_CreatesEmptyDto() {
        // When
        EventFullDto dto = new EventFullDto();

        // Then
        assertThat(dto.getId()).isNull();
        assertThat(dto.getTitle()).isNull();
        assertThat(dto.getState()).isNull();
    }

    @Test
    @DisplayName("Setters должны работать")
    void setters_Work() {
        // Given
        EventFullDto dto = new EventFullDto();

        // When
        dto.setId(1L);
        dto.setTitle("Setter Event");
        dto.setState(EventState.PENDING);
        dto.setViews(500L);
        dto.setConfirmedRequests(10);

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Setter Event");
        assertThat(dto.getState()).isEqualTo(EventState.PENDING);
        assertThat(dto.getViews()).isEqualTo(500L);
        assertThat(dto.getConfirmedRequests()).isEqualTo(10);
    }
}
