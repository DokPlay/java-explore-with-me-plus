package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;

/**
 * Short event representation for the public API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventShortDto {

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
     * Event date and time.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    /**
     * Initiator.
     */
    private UserShortDto initiator;

    /**
     * Paid flag.
     */
    private Boolean paid;

    /**
     * Title.
     */
    private String title;

    /**
     * View count.
     */
    private Long views;
}
