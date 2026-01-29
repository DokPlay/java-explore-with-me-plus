package ru.practicum.main.exception;

/**
 * Исключение конфликта (нарушение бизнес-правил).
 * <p>
 * Выбрасывается когда операция нарушает бизнес-правила системы.
 * Обрабатывается в {@link ErrorHandler} и возвращает HTTP 409.
 *
 * <h2>Примеры использования:</h2>
 * <ul>
 *     <li>Попытка изменить опубликованное событие</li>
 *     <li>Публикация события не в статусе PENDING</li>
 *     <li>Отклонение уже опубликованного события</li>
 *     <li>Дублирование уникальных данных</li>
 * </ul>
 *
 * @author ExploreWithMe Team
 * @version 1.0
 * @see ErrorHandler#handleConflictException
 */
public class ConflictException extends RuntimeException {

    /**
     * Создаёт исключение с указанным сообщением.
     *
     * @param message описание конфликта
     */
    public ConflictException(String message) {
        super(message);
    }
}
