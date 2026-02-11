package ru.practicum.main.location.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for location creation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewManagedLocationDto {

    /**
     * Location name.
     */
    @NotBlank(message = "Название локации обязательно")
    @Size(min = 2, max = 120, message = "Название локации должно быть от 2 до 120 символов")
    private String name;

    /**
     * Latitude.
     */
    @NotNull(message = "Широта обязательна")
    @DecimalMin(value = "-90.0", message = "Широта должна быть >= -90")
    @DecimalMax(value = "90.0", message = "Широта должна быть <= 90")
    private Double lat;

    /**
     * Longitude.
     */
    @NotNull(message = "Долгота обязательна")
    @DecimalMin(value = "-180.0", message = "Долгота должна быть >= -180")
    @DecimalMax(value = "180.0", message = "Долгота должна быть <= 180")
    private Double lon;
}
