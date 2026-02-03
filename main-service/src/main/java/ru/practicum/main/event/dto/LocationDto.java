package ru.practicum.main.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for event location coordinates.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDto {

    /**
     * Latitude.
     */
    private Float lat;

    /**
     * Longitude.
     */
    private Float lon;
}
