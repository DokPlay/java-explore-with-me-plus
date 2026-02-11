package ru.practicum.main.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for updating comment text.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCommentDto {

    /**
     * Updated comment text body.
     */
    @NotBlank(message = "Текст комментария обязателен")
    @Size(min = 2, max = 2000, message = "Текст комментария должен быть от 2 до 2000 символов")
    private String text;
}
