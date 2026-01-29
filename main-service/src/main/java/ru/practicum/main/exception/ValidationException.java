package ru.practicum.main.exception;

/**
 * Business-rule validation exception.
 * <p>
 * Used to validate rules that are not covered by Bean Validation.
 * Handled in {@link ErrorHandler} and returns HTTP 400.
 */
public class ValidationException extends RuntimeException {

    /**
     * Creates an exception with the specified message.
     *
     * @param message validation error description
     */
    public ValidationException(String message) {
        super(message);
    }
}
