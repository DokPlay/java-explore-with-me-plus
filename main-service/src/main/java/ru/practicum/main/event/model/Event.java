package ru.practicum.main.event.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;

/**
 * Сущность события.
 */
@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    /**
     * Уникальный идентификатор события.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Краткое описание события (аннотация).
     */
    @Column(nullable = false, length = 2000)
    private String annotation;

    /**
     * Категория события.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * Количество одобренных заявок на участие.
     */
    @Column(name = "confirmed_requests")
    @Builder.Default
    private Long confirmedRequests = 0L;

    /**
     * Дата и время создания события.
     */
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    /**
     * Полное описание события.
     */
    @Column(length = 7000)
    private String description;

    /**
     * Дата и время проведения события.
     */
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    /**
     * Инициатор (создатель) события.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    /**
     * Координаты места проведения события.
     */
    @Embedded
    private Location location;

    /**
     * Флаг платности события.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean paid = false;

    /**
     * Лимит участников (0 = без ограничений).
     */
    @Column(name = "participant_limit")
    @Builder.Default
    private Integer participantLimit = 0;

    /**
     * Дата и время публикации события.
     */
    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    /**
     * Требуется ли пре-модерация заявок.
     */
    @Column(name = "request_moderation")
    @Builder.Default
    private Boolean requestModeration = true;

    /**
     * Состояние события.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EventState state = EventState.PENDING;

    /**
     * Заголовок события.
     */
    @Column(nullable = false, length = 120)
    private String title;

    /**
     * Количество просмотров.
     */
    @Builder.Default
    private Long views = 0L;
}
