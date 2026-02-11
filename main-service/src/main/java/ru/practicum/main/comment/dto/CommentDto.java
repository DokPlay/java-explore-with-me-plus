package ru.practicum.main.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.comment.status.CommentStatus;

import java.time.LocalDateTime;

/**
 * Response DTO for comment API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    /**
     * Comment ID.
     */
    private Long id;

    /**
     * Comment text.
     */
    private String text;

    /**
     * Linked event ID.
     */
    private Long eventId;

    /**
     * Author user ID.
     */
    private Long authorId;

    /**
     * Creation timestamp.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    /**
     * Last update timestamp.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedOn;

    /**
     * Moderation status.
     */
    private CommentStatus status;

    /**
     * Optional moderation note.
     */
    private String moderationNote;
}
