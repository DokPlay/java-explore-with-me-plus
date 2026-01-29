package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;

/**
 * Полное DTO события.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFullDto {

    /**
     * Идентификатор события.
     */
    private Long id;

    /**
     * Краткое описание.
     */
    private String annotation;

    /**
     * Категория события.
     */
    private CategoryDto category;

    /**
     * Количество одобренных заявок.
     */
    private Long confirmedRequests;

    /**
     * Дата и время создания.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    /**
     * Полное описание.
     */
    private String description;

    /**
     * Дата и время проведения.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    /**
     * Инициатор события.
     */
    private UserShortDto initiator;

    /**
     * Координаты места проведения.
     */
    private LocationDto location;

    /**
     * Платное ли событие.
     */
    private Boolean paid;

    /**
     * Лимит участников.
     */
    private Integer participantLimit;

    /**
     * Дата и время публикации.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    /**
     * Требуется ли модерация заявок.
     */
    private Boolean requestModeration;

    /**
     * Состояние события.
     */
    private EventState state;

    /**
     * Заголовок.
     */
    private String title;

    /**
     * Количество просмотров.
     */
    private Long views;
}
