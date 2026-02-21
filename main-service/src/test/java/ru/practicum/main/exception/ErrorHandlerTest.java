package ru.practicum.main.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import ru.practicum.main.event.model.Event;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ErrorHandler Tests")
class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    @DisplayName("Должен вернуть 409 при optimistic lock конфликте")
    void handleOptimisticLockingFailureException_ReturnsConflictApiError() {
        ObjectOptimisticLockingFailureException exception =
                new ObjectOptimisticLockingFailureException(Event.class, 1L);

        ApiError apiError = errorHandler.handleOptimisticLockingFailureException(exception);

        assertThat(apiError.getStatus()).isEqualTo(HttpStatus.CONFLICT.name());
        assertThat(apiError.getErrorCode()).isEqualTo("OPTIMISTIC_LOCK_CONFLICT");
        assertThat(apiError.getMessage()).isEqualTo("Конфликт конкурентного обновления");
        assertThat(apiError.getReason()).isEqualTo("Ресурс был изменен параллельно, повторите запрос");
        assertThat(apiError.getErrors()).isNotEmpty();
        assertThat(apiError.getTimestamp()).isNotNull();
    }
}
