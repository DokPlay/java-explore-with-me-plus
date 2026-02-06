package ru.practicum.main.exception;

/**
 * Resource-not-found exception.
 * <p>
 * Thrown when the requested object is not found.
 * Handled in {@link ErrorHandler} and returns HTTP 404.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Creates an exception with the specified message.
     *
     * @param message description of the missing resource
     */
    public NotFoundException(String message) {
        super(message);
    }
}
