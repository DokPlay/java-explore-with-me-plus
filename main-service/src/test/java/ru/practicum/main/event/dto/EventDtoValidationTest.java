package ru.practicum.main.event.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты валидации DTO.
 */
@DisplayName("Event DTO Validation Tests")
class EventDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("NewEventDto Validation")
    class NewEventDtoValidationTests {

        @Test
        @DisplayName("Валидный DTO не должен иметь нарушений")
        void validDto_NoViolations() {
            // Given
            NewEventDto dto = NewEventDto.builder()
                    .title("Valid Title Here")
                    .annotation("This is a valid annotation with at least 20 characters")
                    .description("This is a valid description with at least 20 characters")
                    .category(1L)
                    .eventDate(LocalDateTime.now().plusDays(7))
                    .location(new LocationDto(55.75f, 37.62f))
                    .paid(false)
                    .participantLimit(100)
                    .requestModeration(true)
                    .build();

            // When
            Set<ConstraintViolation<NewEventDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Пустой заголовок должен нарушать валидацию")
        void blankTitle_HasViolation() {
            // Given
            NewEventDto dto = NewEventDto.builder()
                    .title("")
                    .annotation("This is a valid annotation with at least 20 characters")
                    .description("This is a valid description with at least 20 characters")
                    .category(1L)
                    .eventDate(LocalDateTime.now().plusDays(7))
                    .location(new LocationDto(55.75f, 37.62f))
                    .build();

            // When
            Set<ConstraintViolation<NewEventDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .extracting(v -> v.getPropertyPath().toString())
                    .contains("title");
        }

        @Test
        @DisplayName("Слишком короткая аннотация должна нарушать валидацию")
        void shortAnnotation_HasViolation() {
            // Given
            NewEventDto dto = NewEventDto.builder()
                    .title("Valid Title")
                    .annotation("Short")
                    .description("This is a valid description with at least 20 characters")
                    .category(1L)
                    .eventDate(LocalDateTime.now().plusDays(7))
                    .location(new LocationDto(55.75f, 37.62f))
                    .build();

            // When
            Set<ConstraintViolation<NewEventDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .extracting(v -> v.getPropertyPath().toString())
                    .contains("annotation");
        }

        @Test
        @DisplayName("Null категория должна нарушать валидацию")
        void nullCategory_HasViolation() {
            // Given
            NewEventDto dto = NewEventDto.builder()
                    .title("Valid Title")
                    .annotation("This is a valid annotation with at least 20 characters")
                    .description("This is a valid description with at least 20 characters")
                    .category(null)
                    .eventDate(LocalDateTime.now().plusDays(7))
                    .location(new LocationDto(55.75f, 37.62f))
                    .build();

            // When
            Set<ConstraintViolation<NewEventDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .extracting(v -> v.getPropertyPath().toString())
                    .contains("category");
        }

        @Test
        @DisplayName("Null дата события должна нарушать валидацию")
        void nullEventDate_HasViolation() {
            // Given
            NewEventDto dto = NewEventDto.builder()
                    .title("Valid Title")
                    .annotation("This is a valid annotation with at least 20 characters")
                    .description("This is a valid description with at least 20 characters")
                    .category(1L)
                    .eventDate(null)
                    .location(new LocationDto(55.75f, 37.62f))
                    .build();

            // When
            Set<ConstraintViolation<NewEventDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .extracting(v -> v.getPropertyPath().toString())
                    .contains("eventDate");
        }

        @Test
        @DisplayName("Null локация должна нарушать валидацию")
        void nullLocation_HasViolation() {
            // Given
            NewEventDto dto = NewEventDto.builder()
                    .title("Valid Title")
                    .annotation("This is a valid annotation with at least 20 characters")
                    .description("This is a valid description with at least 20 characters")
                    .category(1L)
                    .eventDate(LocalDateTime.now().plusDays(7))
                    .location(null)
                    .build();

            // When
            Set<ConstraintViolation<NewEventDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .extracting(v -> v.getPropertyPath().toString())
                    .contains("location");
        }

        @Test
        @DisplayName("Слишком длинный заголовок должен нарушать валидацию")
        void tooLongTitle_HasViolation() {
            // Given
            String longTitle = "A".repeat(121); // больше 120 символов
            NewEventDto dto = NewEventDto.builder()
                    .title(longTitle)
                    .annotation("This is a valid annotation with at least 20 characters")
                    .description("This is a valid description with at least 20 characters")
                    .category(1L)
                    .eventDate(LocalDateTime.now().plusDays(7))
                    .location(new LocationDto(55.75f, 37.62f))
                    .build();

            // When
            Set<ConstraintViolation<NewEventDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .extracting(v -> v.getPropertyPath().toString())
                    .contains("title");
        }
    }

    @Nested
    @DisplayName("LocationDto Validation")
    class LocationDtoValidationTests {

        @Test
        @DisplayName("Валидная локация не должна иметь нарушений")
        void validLocation_NoViolations() {
            // Given
            LocationDto dto = new LocationDto(55.75f, 37.62f);

            // When
            Set<ConstraintViolation<LocationDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Null широта должна нарушать валидацию")
        void nullLat_HasViolation() {
            // Given
            LocationDto dto = new LocationDto(null, 37.62f);

            // When
            Set<ConstraintViolation<LocationDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .extracting(v -> v.getPropertyPath().toString())
                    .contains("lat");
        }

        @Test
        @DisplayName("Null долгота должна нарушать валидацию")
        void nullLon_HasViolation() {
            // Given
            LocationDto dto = new LocationDto(55.75f, null);

            // When
            Set<ConstraintViolation<LocationDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .extracting(v -> v.getPropertyPath().toString())
                    .contains("lon");
        }
    }
}
