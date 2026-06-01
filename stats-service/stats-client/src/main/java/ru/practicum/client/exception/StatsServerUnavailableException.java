package ru.practicum.client.exception;

public class StatsServerUnavailableException extends RuntimeException {

    public StatsServerUnavailableException(String message) {
        super(message);
    }
}
