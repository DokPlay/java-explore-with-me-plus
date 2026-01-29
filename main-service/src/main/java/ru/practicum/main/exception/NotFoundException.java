package ru.practicum.main.exception;

/**
 * Исключение "Ресурс не найден".
 * <p>
 * Выбрасывается когда запрашиваемый ресурс не существует в системе.
 * Обрабатывается в {@link ErrorHandler} и возвращает HTTP 404.
 *
 * <h2>Примеры использования:</h2>
 * <ul>
 *     <li>Событие с указанным ID не найдено</li>
 *     <li>Пользователь не существует</li>
 *     <li>Категория не найдена</li>
 * </ul>
 *
 * @author ExploreWithMe Team
 * @version 1.0
 * @see ErrorHandler#handleNotFoundException
 */
public class NotFoundException extends RuntimeException {

    /**
     * Создаёт исключение с указанным сообщением.
     *
     * @param message описание ненайденного ресурса
     */
    public NotFoundException(String message) {
        super(message);
    }
}
