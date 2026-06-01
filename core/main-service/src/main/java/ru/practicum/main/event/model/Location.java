package ru.practicum.main.event.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class for storing event location coordinates.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    /**
     * Latitude.
     */
    @NotNull
    @Column(name = "lat", nullable = false)
    private Float lat;

    /**
     * Longitude.
     */
    @NotNull
    @Column(name = "lon", nullable = false)
    private Float lon;
}
