package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO для обновления события администратором.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventAdminRequest {

    /**
     * Новая аннотация.
     */
    @Size(min = 20, max = 2000, message = "Аннотация должна быть от 20 до 2000 символов")
    private String annotation;

    /**
     * Новая категория.
     */
    private Long category;

    /**
     * Новое описание.
     */
    @Size(min = 20, max = 7000, message = "Описание должно быть от 20 до 7000 символов")
    private String description;

    /**
     * Новая дата события.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    /**
     * Новые координаты.
     */
    private LocationDto location;

    /**
     * Новый флаг платности.
     */
    private Boolean paid;

    /**
     * Новый лимит участников.
     */
    @PositiveOrZero(message = "Лимит участников не может быть отрицательным")
    private Integer participantLimit;

    /**
     * Требуется ли модерация заявок.
     */
    private Boolean requestModeration;

    /**
     * Действие администратора.
     */
    private StateAction stateAction;

    /**
     * Новый заголовок.
     */
    @Size(min = 3, max = 120, message = "Заголовок должен быть от 3 до 120 символов")
    private String title;

    /**
     * Действия администратора над событием.
     */
    public enum StateAction {
        PUBLISH_EVENT,
        REJECT_EVENT
    }
}
