package ru.practicum.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Кастомные исключения
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(ValidationException ex) {
        log.warn("Validation exception: {}", ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(NotFoundException ex) {
        log.warn("Not found: {}", ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleForbiddenException(ForbiddenException ex) {
        log.warn("Forbidden: {}", ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflictException(ConflictException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestException(BadRequestException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    // Валидация @Valid в DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        
        log.warn("Validation errors: {}", errors);
        
        return Map.of(
            "errors", errors,
            "message", "Ошибка валидации",
            "timestamp", LocalDateTime.now(),
            "status", "BAD_REQUEST"
        );
    }

    // Валидация параметров запроса (@RequestParam)
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(violation -> String.format("%s: %s", 
                    violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.toList());
        
        log.warn("Constraint violations: {}", errors);
        
        return Map.of(
            "errors", errors,
            "message", "Нарушение ограничений валидации",
            "timestamp", LocalDateTime.now(),
            "status", "BAD_REQUEST"
        );
    }

    // Некорректный JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = "Некорректный JSON в теле запроса";
        
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) ex.getCause();
            message = String.format("Некорректный формат для поля '%s': ожидается %s", 
                ife.getPath().get(0).getFieldName(), ife.getTargetType().getSimpleName());
        }
        
        log.warn("Invalid JSON: {}", message);
        return Map.of("error", message);
    }

    // Отсутствуют обязательные параметры
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String message = String.format("Отсутствует обязательный параметр: %s", ex.getParameterName());
        log.warn("Missing parameter: {}", ex.getParameterName());
        return Map.of("error", message);
    }

    // Неверный тип параметра
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Параметр '%s' должен быть типа '%s'", 
            ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        log.warn("Type mismatch: {}", message);
        return Map.of("error", message);
    }

    // Нарушение целостности данных в БД
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMessage(), ex);
        return Map.of("error", "Нарушение целостности данных");
    }

    // Все остальные исключения
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage(), ex);
        return Map.of("error", "Внутренняя ошибка сервера");
    }
}