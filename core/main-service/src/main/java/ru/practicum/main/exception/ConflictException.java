package ru.practicum.main.exception;

/**
 * Business-rule conflict exception.
 * <p>
 * Thrown when an operation violates domain rules.
 * Handled in {@link ErrorHandler} and returns HTTP 409.
 */
public class ConflictException extends RuntimeException {

    /**
     * Creates an exception with the specified message.
     *
     * @param message conflict description
     */
    public ConflictException(String message) {
        super(message);
    }
}
