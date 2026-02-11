package ru.practicum.main.comment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for comment moderation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUpdateCommentRequest {

    /**
     * Moderation action to apply.
     */
    @NotNull(message = "Действие модерации обязательно")
    private Action action;

    /**
     * Optional moderation note.
     */
    @Size(max = 1000, message = "Комментарий модератора не должен превышать 1000 символов")
    private String moderationNote;

    /**
     * Available moderation actions.
     */
    public enum Action {
        PUBLISH,
        REJECT
    }
}
