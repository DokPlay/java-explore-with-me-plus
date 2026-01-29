package ru.practicum.main.exception;

/**
 * Исключение "Не найдено".
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
