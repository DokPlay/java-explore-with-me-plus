package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;

/**
 * Краткое DTO события для публичного API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventShortDto {

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
     * Дата и время проведения.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    /**
     * Инициатор события.
     */
    private UserShortDto initiator;

    /**
     * Платное ли событие.
     */
    private Boolean paid;

    /**
     * Заголовок.
     */
    private String title;

    /**
     * Количество просмотров.
     */
    private Long views;
}
