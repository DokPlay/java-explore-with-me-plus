package ru.practicum.main.event.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit тесты для модели Location (Embeddable).
 */
@DisplayName("Location Model Tests")
class LocationTest {

    @Test
    @DisplayName("Должен создать локацию через builder")
    void builderCreatesLocation() {
        // When
        Location location = Location.builder()
                .lat(55.75f)
                .lon(37.62f)
                .build();

        // Then
        assertThat(location.getLat()).isEqualTo(55.75f);
        assertThat(location.getLon()).isEqualTo(37.62f);
    }

    @Test
    @DisplayName("Должен создать локацию через no-args конструктор")
    void noArgsConstructorCreatesEmptyLocation() {
        // When
        Location location = new Location();

        // Then
        assertThat(location.getLat()).isNull();
        assertThat(location.getLon()).isNull();
    }

    @Test
    @DisplayName("Setters должны устанавливать значения")
    void settersSetValues() {
        // Given
        Location location = new Location();

        // When
        location.setLat(40.7128f);
        location.setLon(-74.0060f);

        // Then
        assertThat(location.getLat()).isEqualTo(40.7128f);
        assertThat(location.getLon()).isEqualTo(-74.0060f);
    }

    @Test
    @DisplayName("Должен работать с отрицательными координатами")
    void negativeCoordinatesWork() {
        // When
        Location location = Location.builder()
                .lat(-33.8688f)
                .lon(151.2093f)
                .build();

        // Then
        assertThat(location.getLat()).isNegative();
        assertThat(location.getLon()).isPositive();
    }

    @Test
    @DisplayName("AllArgsConstructor должен создать локацию")
    void allArgsConstructorCreatesLocation() {
        // When
        Location location = new Location(55.75f, 37.62f);

        // Then
        assertThat(location.getLat()).isEqualTo(55.75f);
        assertThat(location.getLon()).isEqualTo(37.62f);
    }
}
