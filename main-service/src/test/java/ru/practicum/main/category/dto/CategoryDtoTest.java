package ru.practicum.main.category.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit тесты для CategoryDto с валидацией.
 */
@DisplayName("CategoryDto Tests")
class CategoryDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Конструкторы и Builder")
    class ConstructorTests {

        @Test
        @DisplayName("Должен создать DTO через конструктор")
        void constructor_CreatesDto() {
            // When
            CategoryDto dto = new CategoryDto(1L, "Test Category");

            // Then
            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getName()).isEqualTo("Test Category");
        }

        @Test
        @DisplayName("Должен создать DTO через builder")
        void builder_CreatesDto() {
            // When
            CategoryDto dto = CategoryDto.builder()
                    .id(1L)
                    .name("Builder Category")
                    .build();

            // Then
            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getName()).isEqualTo("Builder Category");
        }

        @Test
        @DisplayName("Должен создать пустой DTO через no-args конструктор")
        void noArgsConstructor_CreatesEmptyDto() {
            // When
            CategoryDto dto = new CategoryDto();

            // Then
            assertThat(dto.getId()).isNull();
            assertThat(dto.getName()).isNull();
        }
    }

    @Nested
    @DisplayName("Валидация")
    class ValidationTests {

        @Test
        @DisplayName("Валидный DTO не должен иметь нарушений")
        void validDto_NoViolations() {
            // Given
            CategoryDto dto = new CategoryDto(1L, "Valid Category");

            // When
            Set<ConstraintViolation<CategoryDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Пустое название должно нарушать валидацию")
        void blankName_HasViolation() {
            // Given
            CategoryDto dto = new CategoryDto(1L, "");

            // When
            Set<ConstraintViolation<CategoryDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .extracting(v -> v.getPropertyPath().toString())
                    .contains("name");
        }

        @Test
        @DisplayName("Null название должно нарушать валидацию")
        void nullName_HasViolation() {
            // Given
            CategoryDto dto = new CategoryDto(1L, null);

            // When
            Set<ConstraintViolation<CategoryDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("Слишком длинное название должно нарушать валидацию")
        void tooLongName_HasViolation() {
            // Given
            String longName = "A".repeat(51); // больше 50 символов
            CategoryDto dto = new CategoryDto(1L, longName);

            // When
            Set<ConstraintViolation<CategoryDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .extracting(v -> v.getPropertyPath().toString())
                    .contains("name");
        }

        @Test
        @DisplayName("Название максимальной длины должно быть валидным")
        void maxLengthName_NoViolation() {
            // Given
            String maxName = "A".repeat(50); // ровно 50 символов
            CategoryDto dto = new CategoryDto(1L, maxName);

            // When
            Set<ConstraintViolation<CategoryDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Название минимальной длины должно быть валидным")
        void minLengthName_NoViolation() {
            // Given
            CategoryDto dto = new CategoryDto(1L, "A"); // 1 символ

            // When
            Set<ConstraintViolation<CategoryDto>> violations = validator.validate(dto);

            // Then
            assertThat(violations).isEmpty();
        }
    }
}
