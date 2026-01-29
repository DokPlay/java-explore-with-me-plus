package ru.practicum.main.exception;

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
 * Глобальный обработчик исключений для всех контроллеров.
 * <p>
 * Преобразует исключения в унифицированный формат {@link ApiError} согласно спецификации API.
 *
 * <h2>Обрабатываемые исключения:</h2>
 * <table border="1">
 *     <tr><th>Исключение</th><th>HTTP статус</th><th>Описание</th></tr>
 *     <tr><td>ValidationException</td><td>400</td><td>Ошибка валидации бизнес-правил</td></tr>
 *     <tr><td>MethodArgumentNotValidException</td><td>400</td><td>Ошибка валидации DTO (Bean Validation)</td></tr>
 *     <tr><td>MissingServletRequestParameterException</td><td>400</td><td>Отсутствует обязательный параметр</td></tr>
 *     <tr><td>NotFoundException</td><td>404</td><td>Ресурс не найден</td></tr>
 *     <tr><td>ConflictException</td><td>409</td><td>Конфликт бизнес-правил</td></tr>
 *     <tr><td>DataIntegrityViolationException</td><td>409</td><td>Нарушение целостности БД</td></tr>
 *     <tr><td>Exception</td><td>500</td><td>Неизвестная ошибка сервера</td></tr>
 * </table>
 *
 * @author ExploreWithMe Team
 * @version 1.0
 * @see ApiError
 */
@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    /**
     * Обрабатывает ошибки валидации бизнес-правил.
     * <p>
     * Примеры: некорректная дата события, неверный диапазон дат.
     *
     * @param e исключение валидации
     * @return объект ошибки с HTTP 400
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(ValidationException e) {
        log.warn("Ошибка валидации: {}", e.getMessage());
        return ApiError.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason("Incorrectly made request.")
                .status("BAD_REQUEST")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Обрабатывает ошибки валидации DTO (Bean Validation).
     * <p>
     * Срабатывает при нарушении аннотаций @NotNull, @Size, @Min и т.д.
     *
     * @param e исключение валидации аргументов
     * @return объект ошибки с HTTP 400 и списком нарушенных полей
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("Field: %s. Error: %s. Value: %s",
                        error.getField(), error.getDefaultMessage(), error.getRejectedValue()))
                .collect(Collectors.joining("; "));
        log.warn("Ошибка валидации аргументов: {}", errors);
        return ApiError.builder()
                .errors(List.of(errors))
                .message(errors)
                .reason("Incorrectly made request.")
                .status("BAD_REQUEST")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Обработка MissingServletRequestParameterException.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("Отсутствует обязательный параметр: {}", e.getMessage());
        return ApiError.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason("Incorrectly made request.")
                .status("BAD_REQUEST")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Обработка NotFoundException.
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        log.warn("Объект не найден: {}", e.getMessage());
        return ApiError.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason("The required object was not found.")
                .status("NOT_FOUND")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Обработка ConflictException.
     */
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(ConflictException e) {
        log.warn("Конфликт: {}", e.getMessage());
        return ApiError.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason("For the requested operation the conditions are not met.")
                .status("CONFLICT")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Обработка DataIntegrityViolationException.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.warn("Нарушение целостности данных: {}", e.getMessage());
        return ApiError.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason("Integrity constraint has been violated.")
                .status("CONFLICT")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Обработка остальных исключений.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(Exception e) {
        log.error("Внутренняя ошибка сервера: ", e);
        return ApiError.builder()
                .errors(List.of(e.getMessage()))
                .message("An unexpected error occurred")
                .reason("Internal server error")
                .status("INTERNAL_SERVER_ERROR")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
