package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;

/**
 * Full event representation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFullDto {

    /**
     * Identifier.
     */
    private Long id;

    /**
     * Short description.
     */
    private String annotation;

    /**
     * Event category.
     */
    private CategoryDto category;

    /**
     * Number of approved requests.
     */
    private Long confirmedRequests;

    /**
     * Creation date and time.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    /**
     * Full description.
     */
    private String description;

    /**
     * Event date and time.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    /**
     * Initiator.
     */
    private UserShortDto initiator;

    /**
     * Venue coordinates.
     */
    private LocationDto location;

    /**
     * Paid flag.
     */
    private Boolean paid;

    /**
     * Participant limit.
     */
    private Integer participantLimit;

    /**
     * Publication date and time.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    /**
     * Request moderation flag.
     */
    private Boolean requestModeration;

    /**
     * State.
     */
    private EventState state;

    /**
     * Title.
     */
    private String title;

    /**
     * View count.
     */
    private Long views;
}
