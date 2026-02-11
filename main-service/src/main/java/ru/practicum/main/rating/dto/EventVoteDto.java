package ru.practicum.main.rating.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.rating.status.VoteType;

import java.time.LocalDateTime;

/**
 * DTO for user vote response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventVoteDto {

    /**
     * Vote ID.
     */
    private Long id;

    /**
     * Event ID.
     */
    private Long eventId;

    /**
     * User ID.
     */
    private Long userId;

    /**
     * Vote type.
     */
    private VoteType vote;

    /**
     * Creation time.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    /**
     * Last update time.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedOn;
}
