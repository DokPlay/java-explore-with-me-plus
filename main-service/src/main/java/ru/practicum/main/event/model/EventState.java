package ru.practicum.main.event.model;

/**
 * Состояния жизненного цикла события.
 */
public enum EventState {
    /**
     * Ожидает модерации.
     */
    PENDING,

    /**
     * Опубликовано.
     */
    PUBLISHED,

    /**
     * Отменено.
     */
    CANCELED
}
