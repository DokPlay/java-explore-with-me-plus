package ru.practicum.main.exception;

/**
 * Исключение валидации данных.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
