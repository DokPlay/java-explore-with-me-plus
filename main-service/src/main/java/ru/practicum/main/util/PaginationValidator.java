package ru.practicum.main.util;

import ru.practicum.main.exception.ValidationException;

public final class PaginationValidator {

    private PaginationValidator() {
    }

    public static void validatePagination(int from, int size) {
        if (from < 0) {
            throw new ValidationException("from must be greater than or equal to 0");
        }
        if (size <= 0) {
            throw new ValidationException("size must be greater than 0");
        }
    }
}
