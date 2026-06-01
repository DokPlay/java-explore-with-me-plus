package ru.practicum.main.event.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "Широта обязательна")
    @DecimalMin(value = "-90.0", message = "Широта должна быть не меньше -90")
    @DecimalMax(value = "90.0", message = "Широта должна быть не больше 90")
    private Float lat;

    /**
     * Longitude.
     */
    @NotNull(message = "Долгота обязательна")
    @DecimalMin(value = "-180.0", message = "Долгота должна быть не меньше -180")
    @DecimalMax(value = "180.0", message = "Долгота должна быть не больше 180")
    private Float lon;
}
