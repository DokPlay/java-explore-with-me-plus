package ru.practicum.main.user.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link User} model.
 */
@DisplayName("User Model Tests")
class UserTest {

    @Test
    @DisplayName("Должен создать пользователя через конструктор")
    void constructor_CreatesUser() {
        // Action
        User user = new User(1L, "Test User", "test@test.com");

        // Assert
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Test User");
        assertThat(user.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("Должен создать пользователя через builder")
    void builder_CreatesUser() {
        // Action
        User user = User.builder()
                .id(1L)
                .name("Builder User")
                .email("builder@test.com")
                .build();

        // Assert
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Builder User");
        assertThat(user.getEmail()).isEqualTo("builder@test.com");
    }

    @Test
    @DisplayName("Должен создать пустого пользователя через no-args конструктор")
    void noArgsConstructor_CreatesEmptyUser() {
        // Action
        User user = new User();

        // Assert
        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isNull();
        assertThat(user.getEmail()).isNull();
    }

    @Test
    @DisplayName("Setters должны устанавливать значения")
    void setters_SetValues() {
        // Setup
        User user = new User();

        // Action
        user.setId(1L);
        user.setName("Setter User");
        user.setEmail("setter@test.com");

        // Assert
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Setter User");
        assertThat(user.getEmail()).isEqualTo("setter@test.com");
    }

    @Test
    @DisplayName("Getters должны возвращать значения")
    void getters_ReturnValues() {
        // Setup
        User user = new User(1L, "Getter User", "getter@test.com");

        // Assert
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Getter User");
        assertThat(user.getEmail()).isEqualTo("getter@test.com");
    }
}
