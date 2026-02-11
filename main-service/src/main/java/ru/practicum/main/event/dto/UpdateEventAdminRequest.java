package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO for updating an event by an administrator.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventAdminRequest {

    /**
     * Updated annotation.
     */
    @Size(min = 20, max = 2000, message = "Аннотация должна быть от 20 до 2000 символов")
    private String annotation;

    /**
     * Updated category.
     */
    private Long category;

    /**
     * Updated description.
     */
    @Size(min = 20, max = 7000, message = "Описание должно быть от 20 до 7000 символов")
    private String description;

    /**
     * Updated event date.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    /**
     * Updated coordinates.
     */
    private LocationDto location;

    /**
     * Updated paid flag.
     */
    private Boolean paid;

    /**
     * Updated participant limit.
     */
    @PositiveOrZero(message = "Лимит участников не может быть отрицательным")
    private Integer participantLimit;

    /**
     * Request moderation flag.
     */
    private Boolean requestModeration;

    /**
     * Administrator action.
     */
    private StateAction stateAction;

    /**
     * Updated title.
     */
    @Size(min = 3, max = 120, message = "Заголовок должен быть от 3 до 120 символов")
    private String title;

    /**
     * Moderation note from administrator.
     */
    @Pattern(regexp = ".*\\S.*", message = "Комментарий модерации не может состоять только из пробелов")
    @Size(max = 1000, message = "Комментарий модерации не должен превышать 1000 символов")
    private String moderationNote;

    /**
     * Administrator actions for an event.
     */
    public enum StateAction {
        PUBLISH_EVENT,
        REJECT_EVENT
    }
}
