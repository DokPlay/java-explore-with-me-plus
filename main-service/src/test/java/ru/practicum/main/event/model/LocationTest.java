package ru.practicum.main.event.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link Location} model.
 */
@DisplayName("Location Model Tests")
class LocationTest {

    @Test
    @DisplayName("Должен создать локацию через builder")
    void builderCreatesLocation() {
        // Action
        Location location = Location.builder()
                .lat(55.75f)
                .lon(37.62f)
                .build();

        // Assert
        assertThat(location.getLat()).isEqualTo(55.75f);
        assertThat(location.getLon()).isEqualTo(37.62f);
    }

    @Test
    @DisplayName("Должен создать локацию через no-args конструктор")
    void noArgsConstructorCreatesEmptyLocation() {
        // Action
        Location location = new Location();

        // Assert
        assertThat(location.getLat()).isNull();
        assertThat(location.getLon()).isNull();
    }

    @Test
    @DisplayName("Setters должны устанавливать значения")
    void settersSetValues() {
        // Setup
        Location location = new Location();

        // Action
        location.setLat(40.7128f);
        location.setLon(-74.0060f);

        // Assert
        assertThat(location.getLat()).isEqualTo(40.7128f);
        assertThat(location.getLon()).isEqualTo(-74.0060f);
    }

    @Test
    @DisplayName("Должен работать с отрицательными координатами")
    void negativeCoordinatesWork() {
        // Action
        Location location = Location.builder()
                .lat(-33.8688f)
                .lon(151.2093f)
                .build();

        // Assert
        assertThat(location.getLat()).isNegative();
        assertThat(location.getLon()).isPositive();
    }

    @Test
    @DisplayName("AllArgsConstructor должен создать локацию")
    void allArgsConstructorCreatesLocation() {
        // Action
        Location location = new Location(55.75f, 37.62f);

        // Assert
        assertThat(location.getLat()).isEqualTo(55.75f);
        assertThat(location.getLon()).isEqualTo(37.62f);
    }
}
