package ru.practicum.main.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit тесты для UserShortDto.
 */
@DisplayName("UserShortDto Tests")
class UserShortDtoTest {

    @Test
    @DisplayName("Должен создать DTO через конструктор")
    void constructor_CreatesDto() {
        // When
        UserShortDto dto = new UserShortDto(1L, "Test User");

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test User");
    }

    @Test
    @DisplayName("Должен создать DTO через builder")
    void builder_CreatesDto() {
        // When
        UserShortDto dto = UserShortDto.builder()
                .id(1L)
                .name("Builder User")
                .build();

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Builder User");
    }

    @Test
    @DisplayName("Должен создать пустой DTO через no-args конструктор")
    void noArgsConstructor_CreatesEmptyDto() {
        // When
        UserShortDto dto = new UserShortDto();

        // Then
        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
    }

    @Test
    @DisplayName("Setters и Getters должны работать")
    void settersAndGetters_Work() {
        // Given
        UserShortDto dto = new UserShortDto();

        // When
        dto.setId(1L);
        dto.setName("Setter User");

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Setter User");
    }
}
