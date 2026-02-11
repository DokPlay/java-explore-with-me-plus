package ru.practicum.main.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for controllers.
 * <p>
 * Converts exceptions to the unified {@link ApiError} format.
 * Handles:
 * <ul>
 *     <li>{@link ValidationException} — 400</li>
 *     <li>{@link MethodArgumentNotValidException} — 400</li>
 *     <li>{@link MissingServletRequestParameterException} — 400</li>
 *     <li>{@link NotFoundException} — 404</li>
 *     <li>{@link ConflictException} — 409</li>
 *     <li>{@link DataIntegrityViolationException} — 409</li>
 *     <li>{@link Exception} — 500</li>
 * </ul>
 */
@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    /**
     * Handles business-rule validation errors.
     *
     * @param e validation exception
     * @return error object with HTTP 400
     */

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(ValidationException e) {
        log.warn("ValidationException: {}", e.getMessage(), e);
        return buildApiError(
                "VALIDATION_ERROR",
                e.getMessage(),
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST,
                getExceptionDetails(e)
        );
    }

    /**
     * Handles Bean Validation violations.
     *
     * @param e argument validation exception
     * @return error object with HTTP 400 and error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorDetails = e.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s (значение: '%s')",
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()))
                .collect(Collectors.joining("; "));

        log.warn("MethodArgumentNotValidException: {}", errorDetails, e);

        return buildApiError(
                "ARGUMENT_VALIDATION_FAILED",
                "Ошибка валидации аргументов",
                errorDetails,
                HttpStatus.BAD_REQUEST,
                getExceptionDetails(e)
        );
    }
    /**
     * Handles Bean Validation violations for request parameters.
     */

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(ConstraintViolationException e) {
        String errorDetails = e.getConstraintViolations().stream()
                .map(violation -> String.format("%s: %s (значение: %s)",
                        violation.getPropertyPath(),
                        violation.getMessage(),
                        violation.getInvalidValue()))
                .collect(Collectors.joining("; "));

        log.warn("ConstraintViolationException: {}", errorDetails, e);

        return buildApiError(
                "CONSTRAINT_VIOLATION",
                "Нарушение ограничений валидации",
                errorDetails,
                HttpStatus.BAD_REQUEST,
                getExceptionDetails(e)
        );
    }


    /**
     * Handles missing required request parameter.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String errorDetails = String.format("Не указан обязательный параметр '%s' типа %s",
                e.getParameterName(), e.getParameterType());

        log.warn("MissingServletRequestParameterException: {}", errorDetails, e);

        return buildApiError(
                "MISSING_PARAMETER",
                "Отсутствует обязательный параметр запроса",
                errorDetails,
                HttpStatus.BAD_REQUEST,
                getExceptionDetails(e)
        );
    }

    /**
     * Handles missing required resource.
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        log.warn("NotFoundException: {}", e.getMessage(), e);

        return buildApiError(
                "RESOURCE_NOT_FOUND",
                "Запрашиваемый ресурс не найден",
                e.getMessage(),
                HttpStatus.NOT_FOUND,
                getExceptionDetails(e)
        );
    }

    /**
     * Handles business-rule conflicts.
     */
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(ConflictException e) {
        log.warn("ConflictException: {}", e.getMessage(), e);

        return buildApiError(
                "BUSINESS_CONFLICT",
                "Конфликт бизнес-логики",
                e.getMessage(),
                HttpStatus.CONFLICT,
                getExceptionDetails(e)
        );
    }

    /**
     * Handles data integrity violations.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String rootCauseMessage = getRootCauseMessage(e);
        log.warn("DataIntegrityViolationException: {}", rootCauseMessage, e);

        return buildApiError(
                "DATA_INTEGRITY_VIOLATION",
                "Нарушение целостности данных",
                rootCauseMessage,
                HttpStatus.CONFLICT,
                getExceptionDetails(e)
        );
    }

    /**
     * Handles unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(Exception e) {
        log.error("Unexpected error: ", e);

        return buildApiError(
                "INTERNAL_SERVER_ERROR",
                "Внутренняя ошибка сервера",
                "Произошла непредвиденная ошибка",
                HttpStatus.INTERNAL_SERVER_ERROR,
                getExceptionDetails(e)
        );
    }

    private ApiError buildApiError(String errorCode, String message, String reason,
                                   HttpStatus status, List<String> errors) {
        return ApiError.builder()
                .errorCode(errorCode) // Добавьте это поле в ApiError
                .errors(errors)
                .message(message)
                .reason(reason)
                .status(status.name())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Извлекает детальную информацию об исключении
     */
    private List<String> getExceptionDetails(Throwable e) {
        String details = "Exception: " + e.getClass().getSimpleName()
                + "\nMessage: " + e.getMessage();

        if (e.getCause() != null) {
            details += "\nCause: " + e.getCause().getClass().getSimpleName()
                    + " - " + e.getCause().getMessage();
        }

        return List.of(details);
    }

    /**
     * Получает сообщение корневой причины исключения
     */
    private String getRootCauseMessage(Throwable e) {
        Throwable cause = e;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage() != null ? cause.getMessage() : e.getMessage();
    }
}
