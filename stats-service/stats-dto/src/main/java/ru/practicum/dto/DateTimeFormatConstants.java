package ru.practicum.dto;

/**
 * Constants for date-time formatting across the application.
 * <p>
 * Review fix: Extracted date format pattern to a shared constant
 * to avoid duplication and ensure consistency.
 * </p>
 */
public class DateTimeFormatConstants {
    /** Standard date-time pattern used throughout the stats service. */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private DateTimeFormatConstants() {
    }
}