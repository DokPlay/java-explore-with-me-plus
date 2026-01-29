package ru.practicum.main.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с событиями.
 * <p>
 * Предоставляет методы для:
 * <ul>
 *     <li>Базовые CRUD операции (наследуются от JpaRepository)</li>
 *     <li>Поиск событий пользователя</li>
 *     <li>Административный поиск с фильтрами</li>
 *     <li>Публичный поиск опубликованных событий</li>
 * </ul>
 *
 * <h2>Особенности JPQL запросов:</h2>
 * <ul>
 *     <li>Используется CAST для корректной обработки NULL параметров в PostgreSQL</li>
 *     <li>Публичный поиск возвращает только события со статусом PUBLISHED</li>
 *     <li>Поддерживается пагинация через Pageable</li>
 * </ul>
 *
 * @author ExploreWithMe Team
 * @version 1.0
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Находит все события пользователя с пагинацией.
     *
     * @param initiatorId ID пользователя-инициатора
     * @param pageable    параметры пагинации
     * @return страница событий пользователя
     */
    Page<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    /**
     * Находит событие по ID и ID инициатора.
     * <p>
     * Используется для проверки прав доступа пользователя к событию.
     *
     * @param eventId ID события
     * @param initiatorId ID инициатора
     * @return событие, если найдено и принадлежит пользователю
     */
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    /**
     * Проверяет существование события в указанной категории.
     * <p>
     * Используется перед удалением категории для проверки связей.
     *
     * @param categoryId ID категории
     * @return true, если есть события в данной категории
     */
    boolean existsByCategoryId(Long categoryId);

    /**
     * Поиск событий для администратора с фильтрами.
     * <p>
     * Возвращает события любого статуса. Все параметры опциональны.
     *
     * @param users      список ID пользователей (NULL = все)
     * @param states     список статусов (NULL = все)
     * @param categories список ID категорий (NULL = все)
     * @param rangeStart начало временного диапазона (NULL = без ограничения)
     * @param rangeEnd   конец временного диапазона (NULL = без ограничения)
     * @param pageable   параметры пагинации
     * @return страница событий, соответствующих фильтрам
     */
    @Query("SELECT e FROM Event e " +
           "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
           "AND (:states IS NULL OR e.state IN :states) " +
           "AND (:categories IS NULL OR e.category.id IN :categories) " +
           "AND (CAST(:rangeStart AS timestamp) IS NULL OR e.eventDate >= :rangeStart) " +
           "AND (CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate <= :rangeEnd)")
    Page<Event> findEventsForAdmin(
            @Param("users") List<Long> users,
            @Param("states") List<EventState> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);

    /**
     * Публичный поиск опубликованных событий с фильтрами.
     * <p>
     * Возвращает только события со статусом PUBLISHED.
     * Поддерживает текстовый поиск по аннотации и описанию (регистронезависимый).
     *
     * @param text          текст для поиска (NULL = без текстового фильтра)
     * @param categories    список ID категорий (NULL = все категории)
     * @param paid          фильтр платности (NULL = все)
     * @param rangeStart    начало временного диапазона
     * @param rangeEnd      конец временного диапазона
     * @param onlyAvailable только события с доступными местами
     * @param pageable      параметры пагинации
     * @return страница опубликованных событий
     */
    @Query("SELECT e FROM Event e " +
           "WHERE e.state = 'PUBLISHED' " +
           "AND (CAST(:text AS string) IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', CAST(:text AS string), '%')) " +
           "OR LOWER(e.description) LIKE LOWER(CONCAT('%', CAST(:text AS string), '%'))) " +
           "AND (:categories IS NULL OR e.category.id IN :categories) " +
           "AND (:paid IS NULL OR e.paid = :paid) " +
           "AND (CAST(:rangeStart AS timestamp) IS NULL OR e.eventDate >= :rangeStart) " +
           "AND (CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate <= :rangeEnd) " +
           "AND (:onlyAvailable = false OR e.participantLimit = 0 " +
           "OR e.confirmedRequests < e.participantLimit)")
    Page<Event> findPublicEvents(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") Boolean onlyAvailable,
            Pageable pageable);

    /**
     * Находит событие по ID и статусу.
     * <p>
     * Используется для получения опубликованного события в Public API.
     *
     * @param id    ID события
     * @param state требуемый статус события
     * @return событие, если найдено с указанным статусом
     */
    Optional<Event> findByIdAndState(Long id, EventState state);

    /**
     * Находит события по списку ID.
     * <p>
     * Используется для пакетной загрузки событий (например, для подборок).
     *
     * @param ids список ID событий
     * @return список найденных событий
     */
    List<Event> findAllByIdIn(List<Long> ids);
}
