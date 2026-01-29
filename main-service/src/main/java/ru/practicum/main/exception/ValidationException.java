package ru.practicum.main.exception;

/**
 * Исключение валидации входных данных.
 * <p>
 * Выбрасывается когда входные данные не проходят валидацию бизнес-правил
 * (в отличие от Bean Validation, которая обрабатывается отдельно).
 * Обрабатывается в {@link ErrorHandler} и возвращает HTTP 400.
 *
 * <h2>Примеры использования:</h2>
 * <ul>
 *     <li>Дата события в прошлом или слишком близко к текущему времени</li>
 *     <li>Некорректный диапазон дат (начало после конца)</li>
 *     <li>Неверный формат данных</li>
 * </ul>
 *
 * @author ExploreWithMe Team
 * @version 1.0
 * @see ErrorHandler#handleValidationException
 */
public class ValidationException extends RuntimeException {

    /**
     * Создаёт исключение с указанным сообщением.
     *
     * @param message описание ошибки валидации
     */
    public ValidationException(String message) {
        super(message);
    }
}
