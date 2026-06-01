package ru.practicum.main.event.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link EventShortDto}.
 */
@DisplayName("EventShortDto Tests")
class EventShortDtoTest {

    @Test
    @DisplayName("Должен создать DTO через builder")
    void builderCreatesDto() {
        // Setup
        LocalDateTime eventDate = LocalDateTime.now().plusDays(7);
        UserShortDto initiator = new UserShortDto(1L, "Test User");
        CategoryDto category = new CategoryDto(1L, "Test Category");

        // Action
        EventShortDto dto = EventShortDto.builder()
                .id(1L)
                .title("Test Event")
                .annotation("Test Annotation")
                .eventDate(eventDate)
                .initiator(initiator)
                .category(category)
                .paid(true)
                .confirmedRequests(25L)
                .views(1000L)
                .build();

        // Assert
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Test Event");
        assertThat(dto.getEventDate()).isEqualTo(eventDate);
        assertThat(dto.getConfirmedRequests()).isEqualTo(25L);
        assertThat(dto.getViews()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("Должен создать пустой DTO")
    void noArgsConstructorCreatesEmptyDto() {
        // Action
        EventShortDto dto = new EventShortDto();

        // Assert
        assertThat(dto.getId()).isNull();
        assertThat(dto.getTitle()).isNull();
    }

    @Test
    @DisplayName("Setters должны работать")
    void settersWork() {
        // Setup
        EventShortDto dto = new EventShortDto();

        // Action
        dto.setId(1L);
        dto.setTitle("Setter Event");
        dto.setViews(500L);
        dto.setPaid(false);

        // Assert
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Setter Event");
        assertThat(dto.getViews()).isEqualTo(500L);
        assertThat(dto.getPaid()).isFalse();
    }
}
