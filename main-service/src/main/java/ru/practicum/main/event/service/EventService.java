package ru.practicum.main.event.service;

import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.event.model.EventState;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для работы с событиями.
 * <p>
 * Предоставляет методы для трёх уровней доступа:
 * <ul>
 *     <li><b>Private API</b> - операции для авторизованных пользователей</li>
 *     <li><b>Admin API</b> - административные операции</li>
 *     <li><b>Public API</b> - публичный доступ без авторизации</li>
 * </ul>
 *
 * @author ExploreWithMe Team
 * @version 1.0
 */
public interface EventService {

    // ==================== Private API ====================
    // Операции для авторизованных пользователей (владельцев событий)

    /**
     * Получает список событий, созданных пользователем.
     *
     * @param userId ID пользователя
     * @param from   начальный индекс для пагинации
     * @param size   количество элементов на странице
     * @return список кратких DTO событий
     */
    List<EventShortDto> getUserEvents(Long userId, int from, int size);

    /**
     * Создаёт новое событие.
     * <p>
     * Событие создаётся в статусе {@link EventState#PENDING} и требует
     * модерации администратором перед публикацией.
     *
     * @param userId      ID пользователя-инициатора
     * @param newEventDto данные нового события
     * @return полное DTO созданного события
     * @throws ru.practicum.main.exception.NotFoundException   если пользователь или категория не найдены
     * @throws ru.practicum.main.exception.ValidationException если дата события некорректна
     */
    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    /**
     * Получает полную информацию о событии пользователя.
     *
     * @param userId  ID пользователя
     * @param eventId ID события
     * @return полное DTO события
     * @throws ru.practicum.main.exception.NotFoundException если событие не найдено
     */
    EventFullDto getUserEventById(Long userId, Long eventId);

    /**
     * Обновляет событие пользователем.
     * <p>
     * Пользователь может изменить только события в статусах:
     * {@link EventState#PENDING} или {@link EventState#CANCELED}.
     *
     * @param userId        ID пользователя
     * @param eventId       ID события
     * @param updateRequest данные для обновления
     * @return полное DTO обновлённого события
     * @throws ru.practicum.main.exception.ConflictException если событие уже опубликовано
     */
    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    // ==================== Admin API ====================
    // Административные операции для модерации и управления событиями

    /**
     * Поиск событий с фильтрами (для администратора).
     * <p>
     * Возвращает полную информацию о событиях, включая неопубликованные.
     *
     * @param users      список ID пользователей (опционально)
     * @param states     список статусов событий (опционально)
     * @param categories список ID категорий (опционально)
     * @param rangeStart начало временного диапазона (опционально)
     * @param rangeEnd   конец временного диапазона (опционально)
     * @param from       начальный индекс для пагинации
     * @param size       количество элементов на странице
     * @return список полных DTO событий
     */
    List<EventFullDto> searchEventsForAdmin(
            List<Long> users,
            List<EventState> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size);

    /**
     * Обновляет событие администратором.
     * <p>
     * Администратор может:
     * <ul>
     *     <li>Опубликовать событие (PUBLISH_EVENT) - только для статуса PENDING</li>
     *     <li>Отклонить событие (REJECT_EVENT) - только для неопубликованных событий</li>
     *     <li>Изменить любые поля события</li>
     * </ul>
     *
     * @param eventId       ID события
     * @param updateRequest данные для обновления
     * @return полное DTO обновлённого события
     * @throws ru.practicum.main.exception.ConflictException при нарушении бизнес-правил
     */
    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest);

    // ==================== Public API ====================
    // Публичный доступ к событиям (без авторизации)

    /**
     * Публичный поиск событий.
     * <p>
     * Возвращает только опубликованные события ({@link EventState#PUBLISHED}).
     * Автоматически сохраняет статистику просмотров.
     *
     * @param text          текст для поиска в названии и описании (опционально)
     * @param categories    список ID категорий (опционально)
     * @param paid          фильтр по платности (опционально)
     * @param rangeStart    начало временного диапазона (опционально)
     * @param rangeEnd      конец временного диапазона (опционально)
     * @param onlyAvailable только события с доступными местами
     * @param sort          сортировка: EVENT_DATE или VIEWS
     * @param from          начальный индекс для пагинации
     * @param size          количество элементов на странице
     * @param request       HTTP запрос для получения IP клиента
     * @return список кратких DTO событий
     */
    List<EventShortDto> searchPublicEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            int from,
            int size,
            HttpServletRequest request);

    /**
     * Получает опубликованное событие по ID.
     * <p>
     * Автоматически сохраняет статистику просмотра и возвращает
     * актуальное количество просмотров из Stats Service.
     *
     * @param eventId ID события
     * @param request HTTP запрос для получения IP клиента
     * @return полное DTO события
     * @throws ru.practicum.main.exception.NotFoundException если событие не найдено или не опубликовано
     */
    EventFullDto getPublishedEventById(Long eventId, HttpServletRequest request);

    /**
     * Получает событие по ID (для внутреннего использования).
     * <p>
     * Возвращает событие независимо от статуса публикации.
     * Не сохраняет статистику просмотров.
     *
     * @param eventId ID события
     * @return полное DTO события
     * @throws ru.practicum.main.exception.NotFoundException если событие не найдено
     */
    EventFullDto getEventById(Long eventId);
}
