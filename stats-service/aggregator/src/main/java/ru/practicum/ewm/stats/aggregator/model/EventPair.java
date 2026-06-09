package ru.practicum.ewm.stats.aggregator.model;

public record EventPair(long first, long second) {

    public EventPair {
        if (first == second) {
            throw new IllegalArgumentException("Event pair must contain two different events");
        }
        if (first > second) {
            long tmp = first;
            first = second;
            second = tmp;
        }
    }
}
