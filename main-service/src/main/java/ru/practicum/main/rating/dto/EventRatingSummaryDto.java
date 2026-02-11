package ru.practicum.main.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Public event rating summary.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRatingSummaryDto {

    /**
     * Event ID.
     */
    private Long eventId;

    /**
     * Number of likes.
     */
    private Long likes;

    /**
     * Number of dislikes.
     */
    private Long dislikes;

    /**
     * Rating score (likes - dislikes).
     */
    private Long score;
}
