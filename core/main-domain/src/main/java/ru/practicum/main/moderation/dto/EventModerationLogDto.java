package ru.practicum.main.moderation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.moderation.status.EventModerationAction;

import java.time.LocalDateTime;

/**
 * DTO for moderation history entry.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventModerationLogDto {

    /**
     * Log ID.
     */
    private Long id;

    /**
     * Event ID.
     */
    private Long eventId;

    /**
     * Moderation action.
     */
    private EventModerationAction action;

    /**
     * Action note.
     */
    private String note;

    /**
     * Action timestamp.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actedOn;
}
