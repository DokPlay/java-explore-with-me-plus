package ru.practicum.main.location.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for location update.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateManagedLocationDto {

    /**
     * New location name.
     */
    @Size(min = 2, max = 120, message = "Название локации должно быть от 2 до 120 символов")
    private String name;

    /**
     * New latitude.
     */
    @DecimalMin(value = "-90.0", message = "Широта должна быть >= -90")
    @DecimalMax(value = "90.0", message = "Широта должна быть <= 90")
    private Double lat;

    /**
     * New longitude.
     */
    @DecimalMin(value = "-180.0", message = "Долгота должна быть >= -180")
    @DecimalMax(value = "180.0", message = "Долгота должна быть <= 180")
    private Double lon;

    /**
     * Active flag update.
     */
    private Boolean active;
}
