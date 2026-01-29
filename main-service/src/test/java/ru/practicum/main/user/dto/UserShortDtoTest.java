package ru.practicum.main.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link UserShortDto}.
 */
@DisplayName("UserShortDto Tests")
class UserShortDtoTest {

    @Test
    @DisplayName("Должен создать DTO через конструктор")
    void constructor_CreatesDto() {
        // Action
        UserShortDto dto = new UserShortDto(1L, "Test User");

        // Assert
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test User");
    }

    @Test
    @DisplayName("Должен создать DTO через builder")
    void builder_CreatesDto() {
        // Action
        UserShortDto dto = UserShortDto.builder()
                .id(1L)
                .name("Builder User")
                .build();

        // Assert
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Builder User");
    }

    @Test
    @DisplayName("Должен создать пустой DTO через no-args конструктор")
    void noArgsConstructor_CreatesEmptyDto() {
        // Action
        UserShortDto dto = new UserShortDto();

        // Assert
        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
    }

    @Test
    @DisplayName("Setters и Getters должны работать")
    void settersAndGetters_Work() {
        // Setup
        UserShortDto dto = new UserShortDto();

        // Action
        dto.setId(1L);
        dto.setName("Setter User");

        // Assert
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Setter User");
    }
}
