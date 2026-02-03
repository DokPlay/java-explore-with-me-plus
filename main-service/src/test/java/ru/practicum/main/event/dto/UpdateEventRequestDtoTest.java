package ru.practicum.main.event.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link UpdateEventUserRequest} and {@link UpdateEventAdminRequest}.
 */
@DisplayName("UpdateEvent Request DTOs Tests")
class UpdateEventRequestDtoTest {

    @Test
    @DisplayName("UpdateEventUserRequest - Должен создать DTO через builder")
    void updateUserRequestBuilderCreatesDto() {
        // Setup
        LocalDateTime eventDate = LocalDateTime.now().plusDays(7);
        LocationDto location = new LocationDto(55.75f, 37.62f);

        // Action
        UpdateEventUserRequest dto = UpdateEventUserRequest.builder()
                .title("Updated Title")
                .annotation("Updated Annotation that is long enough for validation")
                .description("Updated Description that is long enough for validation")
                .eventDate(eventDate)
                .category(2L)
                .location(location)
                .paid(true)
                .participantLimit(50)
                .requestModeration(false)
                .stateAction(UpdateEventUserRequest.StateAction.SEND_TO_REVIEW)
                .build();

        // Assert
        assertThat(dto.getTitle()).isEqualTo("Updated Title");
        assertThat(dto.getEventDate()).isEqualTo(eventDate);
        assertThat(dto.getCategory()).isEqualTo(2L);
        assertThat(dto.getPaid()).isTrue();
        assertThat(dto.getStateAction()).isEqualTo(UpdateEventUserRequest.StateAction.SEND_TO_REVIEW);
    }

    @Test
    @DisplayName("UpdateEventUserRequest - Должен создать пустой DTO")
    void updateUserRequestNoArgsConstructorCreatesEmptyDto() {
        // Action
        UpdateEventUserRequest dto = new UpdateEventUserRequest();

        // Assert
        assertThat(dto.getTitle()).isNull();
        assertThat(dto.getStateAction()).isNull();
    }

    @Test
    @DisplayName("UpdateEventAdminRequest - Должен создать DTO через builder")
    void updateAdminRequestBuilderCreatesDto() {
        // Setup
        LocalDateTime eventDate = LocalDateTime.now().plusDays(14);
        LocationDto location = new LocationDto(40.71f, -74.00f);

        // Action
        UpdateEventAdminRequest dto = UpdateEventAdminRequest.builder()
                .title("Admin Updated Title")
                .eventDate(eventDate)
                .category(3L)
                .location(location)
                .paid(false)
                .stateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT)
                .build();

        // Assert
        assertThat(dto.getTitle()).isEqualTo("Admin Updated Title");
        assertThat(dto.getEventDate()).isEqualTo(eventDate);
        assertThat(dto.getCategory()).isEqualTo(3L);
        assertThat(dto.getPaid()).isFalse();
        assertThat(dto.getStateAction()).isEqualTo(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);
    }

    @Test
    @DisplayName("StateAction enum значения для User")
    void userStateActionValues() {
        // Action and assert
        assertThat(UpdateEventUserRequest.StateAction.SEND_TO_REVIEW).isNotNull();
        assertThat(UpdateEventUserRequest.StateAction.CANCEL_REVIEW).isNotNull();
        assertThat(UpdateEventUserRequest.StateAction.values()).hasSize(2);
    }

    @Test
    @DisplayName("StateAction enum значения для Admin")
    void adminStateActionValues() {
        // Action and assert
        assertThat(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT).isNotNull();
        assertThat(UpdateEventAdminRequest.StateAction.REJECT_EVENT).isNotNull();
        assertThat(UpdateEventAdminRequest.StateAction.values()).hasSize(2);
    }
}
