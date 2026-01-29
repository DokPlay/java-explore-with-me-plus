package ru.practicum.main.event.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit тесты для UpdateEventUserRequest и UpdateEventAdminRequest.
 */
@DisplayName("UpdateEvent Request DTOs Tests")
class UpdateEventRequestDtoTest {

    @Test
    @DisplayName("UpdateEventUserRequest - Должен создать DTO через builder")
    void updateUserRequest_Builder_CreatesDto() {
        // Given
        LocalDateTime eventDate = LocalDateTime.now().plusDays(7);
        LocationDto location = new LocationDto(55.75f, 37.62f);

        // When
        UpdateEventUserRequest dto = UpdateEventUserRequest.builder()
                .title("Updated Title")
                .annotation("Updated Annotation")
                .description("Updated Description")
                .eventDate(eventDate)
                .category(2L)
                .location(location)
                .paid(true)
                .participantLimit(50)
                .requestModeration(false)
                .stateAction("SEND_TO_REVIEW")
                .build();

        // Then
        assertThat(dto.getTitle()).isEqualTo("Updated Title");
        assertThat(dto.getAnnotation()).isEqualTo("Updated Annotation");
        assertThat(dto.getDescription()).isEqualTo("Updated Description");
        assertThat(dto.getEventDate()).isEqualTo(eventDate);
        assertThat(dto.getCategory()).isEqualTo(2L);
        assertThat(dto.getLocation()).isEqualTo(location);
        assertThat(dto.getPaid()).isTrue();
        assertThat(dto.getParticipantLimit()).isEqualTo(50);
        assertThat(dto.getRequestModeration()).isFalse();
        assertThat(dto.getStateAction()).isEqualTo("SEND_TO_REVIEW");
    }

    @Test
    @DisplayName("UpdateEventUserRequest - Должен создать пустой DTO")
    void updateUserRequest_NoArgsConstructor_CreatesEmptyDto() {
        // When
        UpdateEventUserRequest dto = new UpdateEventUserRequest();

        // Then
        assertThat(dto.getTitle()).isNull();
        assertThat(dto.getStateAction()).isNull();
    }

    @Test
    @DisplayName("UpdateEventAdminRequest - Должен создать DTO через builder")
    void updateAdminRequest_Builder_CreatesDto() {
        // Given
        LocalDateTime eventDate = LocalDateTime.now().plusDays(14);
        LocationDto location = new LocationDto(40.71f, -74.00f);

        // When
        UpdateEventAdminRequest dto = UpdateEventAdminRequest.builder()
                .title("Admin Updated Title")
                .annotation("Admin Updated Annotation")
                .description("Admin Updated Description")
                .eventDate(eventDate)
                .category(3L)
                .location(location)
                .paid(false)
                .participantLimit(200)
                .requestModeration(true)
                .stateAction("PUBLISH_EVENT")
                .build();

        // Then
        assertThat(dto.getTitle()).isEqualTo("Admin Updated Title");
        assertThat(dto.getAnnotation()).isEqualTo("Admin Updated Annotation");
        assertThat(dto.getDescription()).isEqualTo("Admin Updated Description");
        assertThat(dto.getEventDate()).isEqualTo(eventDate);
        assertThat(dto.getCategory()).isEqualTo(3L);
        assertThat(dto.getLocation()).isEqualTo(location);
        assertThat(dto.getPaid()).isFalse();
        assertThat(dto.getParticipantLimit()).isEqualTo(200);
        assertThat(dto.getRequestModeration()).isTrue();
        assertThat(dto.getStateAction()).isEqualTo("PUBLISH_EVENT");
    }

    @Test
    @DisplayName("UpdateEventAdminRequest - Должен создать пустой DTO")
    void updateAdminRequest_NoArgsConstructor_CreatesEmptyDto() {
        // When
        UpdateEventAdminRequest dto = new UpdateEventAdminRequest();

        // Then
        assertThat(dto.getTitle()).isNull();
        assertThat(dto.getStateAction()).isNull();
    }

    @Test
    @DisplayName("UpdateEventUserRequest - Setters должны работать")
    void updateUserRequest_Setters_Work() {
        // Given
        UpdateEventUserRequest dto = new UpdateEventUserRequest();

        // When
        dto.setTitle("Setter Title");
        dto.setStateAction("CANCEL_REVIEW");

        // Then
        assertThat(dto.getTitle()).isEqualTo("Setter Title");
        assertThat(dto.getStateAction()).isEqualTo("CANCEL_REVIEW");
    }

    @Test
    @DisplayName("UpdateEventAdminRequest - Setters должны работать")
    void updateAdminRequest_Setters_Work() {
        // Given
        UpdateEventAdminRequest dto = new UpdateEventAdminRequest();

        // When
        dto.setTitle("Admin Setter Title");
        dto.setStateAction("REJECT_EVENT");

        // Then
        assertThat(dto.getTitle()).isEqualTo("Admin Setter Title");
        assertThat(dto.getStateAction()).isEqualTo("REJECT_EVENT");
    }
}
