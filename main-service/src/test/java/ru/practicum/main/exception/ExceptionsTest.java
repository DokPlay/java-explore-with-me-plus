package ru.practicum.main.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
        void constructor_WithMessage_CreatesException() {
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
        void constructor_WithMessage_CreatesException() {
            // When
            ConflictException exception = new ConflictException("Conflict occurred");

            // Then
            assertThat(exception.getMessage()).isEqualTo("Conflict occurred");
        }

        @Test
        @DisplayName("Должен быть наследником RuntimeException")
        void isRuntimeException() {
            // When
            ConflictException exception = new ConflictException("test");

            // Then
            assertThat(exception).isInstanceOf(RuntimeException.class);
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
        void constructor_WithMessage_CreatesException() {
            // When
            ValidationException exception = new ValidationException("Validation failed");

            // Then
            assertThat(exception.getMessage()).isEqualTo("Validation failed");
        }

        @Test
        @DisplayName("Должен быть наследником RuntimeException")
        void isRuntimeException() {
            // When
            ValidationException exception = new ValidationException("test");

            // Then
            assertThat(exception).isInstanceOf(RuntimeException.class);
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
        void builder_CreatesApiError() {
            // When
            ApiError apiError = ApiError.builder()
                    .status("NOT_FOUND")
                    .reason("Resource not found")
                    .message("Event with id=1 was not found")
                    .timestamp("2024-01-01 12:00:00")
                    .build();

            // Then
            assertThat(apiError.getStatus()).isEqualTo("NOT_FOUND");
            assertThat(apiError.getReason()).isEqualTo("Resource not found");
            assertThat(apiError.getMessage()).isEqualTo("Event with id=1 was not found");
            assertThat(apiError.getTimestamp()).isEqualTo("2024-01-01 12:00:00");
        }

        @Test
        @DisplayName("Должен создать пустой ApiError")
        void noArgsConstructor_CreatesEmptyApiError() {
            // When
            ApiError apiError = new ApiError();

            // Then
            assertThat(apiError.getStatus()).isNull();
            assertThat(apiError.getReason()).isNull();
            assertThat(apiError.getMessage()).isNull();
            assertThat(apiError.getTimestamp()).isNull();
        }

        @Test
        @DisplayName("Setters должны работать")
        void setters_Work() {
            // Given
            ApiError apiError = new ApiError();

            // When
            apiError.setStatus("BAD_REQUEST");
            apiError.setReason("Invalid request");
            apiError.setMessage("Title is required");
            apiError.setTimestamp("2024-01-01 12:00:00");

            // Then
            assertThat(apiError.getStatus()).isEqualTo("BAD_REQUEST");
            assertThat(apiError.getReason()).isEqualTo("Invalid request");
            assertThat(apiError.getMessage()).isEqualTo("Title is required");
            assertThat(apiError.getTimestamp()).isEqualTo("2024-01-01 12:00:00");
        }
    }
}
