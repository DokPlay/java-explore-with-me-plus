package ru.practicum.main.event.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit тесты для EventShortDto.
 */
@DisplayName("EventShortDto Tests")
class EventShortDtoTest {

    @Test
    @DisplayName("Должен создать DTO через builder")
    void builder_CreatesDto() {
        // Given
        LocalDateTime eventDate = LocalDateTime.now().plusDays(7);
        UserShortDto initiator = new UserShortDto(1L, "Test User");
        CategoryDto category = new CategoryDto(1L, "Test Category");

        // When
        EventShortDto dto = EventShortDto.builder()
                .id(1L)
                .title("Test Event")
                .annotation("Test Annotation")
                .eventDate(eventDate)
                .initiator(initiator)
                .category(category)
                .paid(true)
                .confirmedRequests(25)
                .views(1000L)
                .build();

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Test Event");
        assertThat(dto.getAnnotation()).isEqualTo("Test Annotation");
        assertThat(dto.getEventDate()).isEqualTo(eventDate);
        assertThat(dto.getInitiator()).isEqualTo(initiator);
        assertThat(dto.getCategory()).isEqualTo(category);
        assertThat(dto.getPaid()).isTrue();
        assertThat(dto.getConfirmedRequests()).isEqualTo(25);
        assertThat(dto.getViews()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("Должен создать пустой DTO")
    void noArgsConstructor_CreatesEmptyDto() {
        // When
        EventShortDto dto = new EventShortDto();

        // Then
        assertThat(dto.getId()).isNull();
        assertThat(dto.getTitle()).isNull();
    }

    @Test
    @DisplayName("Setters должны работать")
    void setters_Work() {
        // Given
        EventShortDto dto = new EventShortDto();

        // When
        dto.setId(1L);
        dto.setTitle("Setter Event");
        dto.setViews(500L);
        dto.setPaid(false);

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Setter Event");
        assertThat(dto.getViews()).isEqualTo(500L);
        assertThat(dto.getPaid()).isFalse();
    }
}
