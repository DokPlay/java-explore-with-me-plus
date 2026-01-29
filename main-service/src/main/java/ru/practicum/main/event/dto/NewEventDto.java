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
 * DTO для создания нового события.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewEventDto {

    /**
     * Краткое описание события.
     */
    @NotBlank(message = "Аннотация не может быть пустой")
    @Size(min = 20, max = 2000, message = "Аннотация должна быть от 20 до 2000 символов")
    private String annotation;

    /**
     * ID категории события.
     */
    @NotNull(message = "Категория обязательна")
    private Long category;

    /**
     * Полное описание события.
     */
    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 20, max = 7000, message = "Описание должно быть от 20 до 7000 символов")
    private String description;

    /**
     * Дата и время проведения события.
     */
    @NotNull(message = "Дата события обязательна")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    /**
     * Координаты места проведения.
     */
    @NotNull(message = "Локация обязательна")
    private LocationDto location;

    /**
     * Флаг платности события.
     */
    @Builder.Default
    private Boolean paid = false;

    /**
     * Лимит участников (0 = без ограничений).
     */
    @PositiveOrZero(message = "Лимит участников не может быть отрицательным")
    @Builder.Default
    private Integer participantLimit = 0;

    /**
     * Требуется ли пре-модерация заявок.
     */
    @Builder.Default
    private Boolean requestModeration = true;

    /**
     * Заголовок события.
     */
    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 3, max = 120, message = "Заголовок должен быть от 3 до 120 символов")
    private String title;
}
