package ru.practicum.main.event.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit тесты для модели Location.
 */
@DisplayName("Location Model Tests")
class LocationTest {

    @Test
    @DisplayName("Должен создать локацию через builder")
    void builder_CreatesLocation() {
        // When
        Location location = Location.builder()
                .id(1L)
                .lat(55.75f)
                .lon(37.62f)
                .build();

        // Then
        assertThat(location.getId()).isEqualTo(1L);
        assertThat(location.getLat()).isEqualTo(55.75f);
        assertThat(location.getLon()).isEqualTo(37.62f);
    }

    @Test
    @DisplayName("Должен создать локацию через no-args конструктор")
    void noArgsConstructor_CreatesEmptyLocation() {
        // When
        Location location = new Location();

        // Then
        assertThat(location.getId()).isNull();
        assertThat(location.getLat()).isNull();
        assertThat(location.getLon()).isNull();
    }

    @Test
    @DisplayName("Setters должны устанавливать значения")
    void setters_SetValues() {
        // Given
        Location location = new Location();

        // When
        location.setId(1L);
        location.setLat(40.7128f);
        location.setLon(-74.0060f);

        // Then
        assertThat(location.getId()).isEqualTo(1L);
        assertThat(location.getLat()).isEqualTo(40.7128f);
        assertThat(location.getLon()).isEqualTo(-74.0060f);
    }

    @Test
    @DisplayName("Должен работать с отрицательными координатами")
    void negativeCoordinates_Work() {
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
    @DisplayName("Должен работать с нулевыми координатами")
    void zeroCoordinates_Work() {
        // When
        Location location = Location.builder()
                .lat(0.0f)
                .lon(0.0f)
                .build();

        // Then
        assertThat(location.getLat()).isZero();
        assertThat(location.getLon()).isZero();
    }
}
