package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO for creating an event.
 * <p>
 * Used in the Private API; required fields are validated by Bean Validation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewEventDto {

    /**
     * Short description.
     */
    @NotBlank(message = "Аннотация не может быть пустой")
    @Size(min = 20, max = 2000, message = "Аннотация должна быть от 20 до 2000 символов")
    private String annotation;

    /**
     * Category identifier.
     */
    @NotNull(message = "Категория обязательна")
    private Long category;

    /**
     * Full description.
     */
    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 20, max = 7000, message = "Описание должно быть от 20 до 7000 символов")
    private String description;

    /**
     * Event date and time.
     */
    @NotNull(message = "Дата события обязательна")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    /**
     * Venue coordinates.
     */
    @NotNull(message = "Локация обязательна")
    private LocationDto location;

    /**
     * Paid flag.
     */
    @Builder.Default
    private Boolean paid = false;

    /**
     * Participant limit (0 means unlimited).
     */
    @PositiveOrZero(message = "Лимит участников не может быть отрицательным")
    @Builder.Default
    private Integer participantLimit = 0;

    /**
     * Request pre-moderation flag.
     */
    @Builder.Default
    private Boolean requestModeration = true;

    /**
     * Title.
     */
    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 3, max = 120, message = "Заголовок должен быть от 3 до 120 символов")
    private String title;
}
