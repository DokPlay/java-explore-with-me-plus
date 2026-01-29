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
    void builderCreatesDto() {
        // Given
        LocalDateTime eventDate = LocalDateTime.now().plusDays(7);
        LocalDateTime createdOn = LocalDateTime.now();
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
                .initiator(initiator)
                .category(category)
                .location(location)
                .paid(true)
                .participantLimit(100)
                .requestModeration(true)
                .confirmedRequests(25L)
                .views(1000L)
                .state(EventState.PUBLISHED)
                .build();

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Test Event");
        assertThat(dto.getState()).isEqualTo(EventState.PUBLISHED);
        assertThat(dto.getConfirmedRequests()).isEqualTo(25L);
        assertThat(dto.getViews()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("Должен создать пустой DTO")
    void noArgsConstructorCreatesEmptyDto() {
        // When
        EventFullDto dto = new EventFullDto();

        // Then
        assertThat(dto.getId()).isNull();
        assertThat(dto.getTitle()).isNull();
        assertThat(dto.getState()).isNull();
    }

    @Test
    @DisplayName("Setters должны работать")
    void settersWork() {
        // Given
        EventFullDto dto = new EventFullDto();

        // When
        dto.setId(1L);
        dto.setTitle("Setter Event");
        dto.setState(EventState.PENDING);
        dto.setViews(500L);
        dto.setConfirmedRequests(10L);

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Setter Event");
        assertThat(dto.getState()).isEqualTo(EventState.PENDING);
        assertThat(dto.getViews()).isEqualTo(500L);
        assertThat(dto.getConfirmedRequests()).isEqualTo(10L);
    }
}
