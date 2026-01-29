package ru.practicum.main.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit тесты для пользовательских исключений.
 */
@DisplayName("Custom Exceptions Tests")
class ExceptionsTest {

    @Nested
    @DisplayName("NotFoundException")
    class NotFoundExceptionTests {

        @Test
        @DisplayName("Должен создать исключение с сообщением")
        void constructorWithMessageCreatesException() {
            // When
            NotFoundException exception = new NotFoundException("Entity not found");

            // Then
            assertThat(exception.getMessage()).isEqualTo("Entity not found");
        }

        @Test
        @DisplayName("Должен быть наследником RuntimeException")
        void isRuntimeException() {
            // When
            NotFoundException exception = new NotFoundException("test");

            // Then
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("Должен бросаться и перехватываться")
        void canBeThrown() {
            // When/Then
            assertThatThrownBy(() -> {
                throw new NotFoundException("Event with id=1 not found");
            })
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Event with id=1 not found");
        }
    }

    @Nested
    @DisplayName("ConflictException")
    class ConflictExceptionTests {

        @Test
        @DisplayName("Должен создать исключение с сообщением")
        void constructorWithMessageCreatesException() {
            // When
            ConflictException exception = new ConflictException("Conflict occurred");

            // Then
            assertThat(exception.getMessage()).isEqualTo("Conflict occurred");
        }

        @Test
        @DisplayName("Должен бросаться и перехватываться")
        void canBeThrown() {
            // When/Then
            assertThatThrownBy(() -> {
                throw new ConflictException("Email already exists");
            })
                    .isInstanceOf(ConflictException.class)
                    .hasMessage("Email already exists");
        }
    }

    @Nested
    @DisplayName("ValidationException")
    class ValidationExceptionTests {

        @Test
        @DisplayName("Должен создать исключение с сообщением")
        void constructorWithMessageCreatesException() {
            // When
            ValidationException exception = new ValidationException("Validation failed");

            // Then
            assertThat(exception.getMessage()).isEqualTo("Validation failed");
        }

        @Test
        @DisplayName("Должен бросаться и перехватываться")
        void canBeThrown() {
            // When/Then
            assertThatThrownBy(() -> {
                throw new ValidationException("Invalid date format");
            })
                    .isInstanceOf(ValidationException.class)
                    .hasMessage("Invalid date format");
        }
    }

    @Nested
    @DisplayName("ApiError")
    class ApiErrorTests {

        @Test
        @DisplayName("Должен создать ApiError через builder")
        void builderCreatesApiError() {
            // Given
            LocalDateTime timestamp = LocalDateTime.now();

            // When
            ApiError apiError = ApiError.builder()
                    .status("NOT_FOUND")
                    .reason("Resource not found")
                    .message("Event with id=1 was not found")
                    .timestamp(timestamp)
                    .errors(List.of("error1", "error2"))
                    .build();

            // Then
            assertThat(apiError.getStatus()).isEqualTo("NOT_FOUND");
            assertThat(apiError.getReason()).isEqualTo("Resource not found");
            assertThat(apiError.getMessage()).isEqualTo("Event with id=1 was not found");
            assertThat(apiError.getTimestamp()).isEqualTo(timestamp);
            assertThat(apiError.getErrors()).containsExactly("error1", "error2");
        }

        @Test
        @DisplayName("Должен создать пустой ApiError")
        void noArgsConstructorCreatesEmptyApiError() {
            // When
            ApiError apiError = new ApiError();

            // Then
            assertThat(apiError.getStatus()).isNull();
            assertThat(apiError.getMessage()).isNull();
        }
    }
}
