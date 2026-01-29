package ru.practicum.main.exception;

/**
 * Исключение конфликта (нарушение бизнес-правил).
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
