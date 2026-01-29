package ru.practicum.main.event.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit тесты для LocationDto.
 */
@DisplayName("LocationDto Tests")
class LocationDtoTest {

    @Test
    @DisplayName("Должен создать DTO через конструктор")
    void constructor_CreatesDto() {
        // When
        LocationDto dto = new LocationDto(55.75f, 37.62f);

        // Then
        assertThat(dto.getLat()).isEqualTo(55.75f);
        assertThat(dto.getLon()).isEqualTo(37.62f);
    }

    @Test
    @DisplayName("Должен создать DTO через builder")
    void builder_CreatesDto() {
        // When
        LocationDto dto = LocationDto.builder()
                .lat(55.75f)
                .lon(37.62f)
                .build();

        // Then
        assertThat(dto.getLat()).isEqualTo(55.75f);
        assertThat(dto.getLon()).isEqualTo(37.62f);
    }

    @Test
    @DisplayName("Должен создать пустой DTO через no-args конструктор")
    void noArgsConstructor_CreatesEmptyDto() {
        // When
        LocationDto dto = new LocationDto();

        // Then
        assertThat(dto.getLat()).isNull();
        assertThat(dto.getLon()).isNull();
    }

    @Test
    @DisplayName("Setters и Getters должны работать")
    void settersAndGetters_Work() {
        // Given
        LocationDto dto = new LocationDto();

        // When
        dto.setLat(40.7128f);
        dto.setLon(-74.0060f);

        // Then
        assertThat(dto.getLat()).isEqualTo(40.7128f);
        assertThat(dto.getLon()).isEqualTo(-74.0060f);
    }

    @Test
    @DisplayName("Должен работать с отрицательными координатами")
    void negativeCoordinates_Work() {
        // When
        LocationDto dto = new LocationDto(-33.8688f, -151.2093f);

        // Then
        assertThat(dto.getLat()).isNegative();
        assertThat(dto.getLon()).isNegative();
    }
}
