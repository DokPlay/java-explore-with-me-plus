package ru.practicum.main.category.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit тесты для модели Category.
 */
@DisplayName("Category Model Tests")
class CategoryTest {

    @Test
    @DisplayName("Должен создать категорию через конструктор")
    void constructor_CreatesCategory() {
        // When
        Category category = new Category(1L, "Test Category");

        // Then
        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("Test Category");
    }

    @Test
    @DisplayName("Должен создать категорию через builder")
    void builder_CreatesCategory() {
        // When
        Category category = Category.builder()
                .id(1L)
                .name("Builder Category")
                .build();

        // Then
        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("Builder Category");
    }

    @Test
    @DisplayName("Должен создать пустую категорию через no-args конструктор")
    void noArgsConstructor_CreatesEmptyCategory() {
        // When
        Category category = new Category();

        // Then
        assertThat(category.getId()).isNull();
        assertThat(category.getName()).isNull();
    }

    @Test
    @DisplayName("Setters должны устанавливать значения")
    void setters_SetValues() {
        // Given
        Category category = new Category();

        // When
        category.setId(1L);
        category.setName("Setter Category");

        // Then
        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("Setter Category");
    }
}
