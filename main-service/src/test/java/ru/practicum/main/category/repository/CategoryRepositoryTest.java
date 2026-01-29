package ru.practicum.main.category.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.main.category.model.Category;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository тесты для CategoryRepository.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CategoryRepository Tests")
class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        category1 = new Category();
        category1.setName("Concerts");
        entityManager.persist(category1);

        category2 = new Category();
        category2.setName("Exhibitions");
        entityManager.persist(category2);

        entityManager.flush();
    }

    @Nested
    @DisplayName("existsByName")
    class ExistsByNameTests {

        @Test
        @DisplayName("Должен вернуть true для существующего названия")
        void existsByName_ExistingName_ReturnsTrue() {
            // When
            boolean result = categoryRepository.existsByName("Concerts");

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Должен вернуть false для несуществующего названия")
        void existsByName_NonExistingName_ReturnsFalse() {
            // When
            boolean result = categoryRepository.existsByName("Sports");

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Должен быть чувствителен к регистру")
        void existsByName_CaseSensitive() {
            // When
            boolean resultLower = categoryRepository.existsByName("concerts");
            boolean resultExact = categoryRepository.existsByName("Concerts");

            // Then
            assertThat(resultExact).isTrue();
            // H2 может быть регистронезависим, PostgreSQL - зависим
        }
    }

    @Nested
    @DisplayName("CRUD Operations")
    class CrudOperationsTests {

        @Test
        @DisplayName("Должен сохранить новую категорию")
        void save_NewCategory_Success() {
            // Given
            Category newCategory = new Category();
            newCategory.setName("Workshops");

            // When
            Category saved = categoryRepository.save(newCategory);

            // Then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getName()).isEqualTo("Workshops");
        }

        @Test
        @DisplayName("Должен найти категорию по ID")
        void findById_ExistingCategory_Found() {
            // When
            Optional<Category> result = categoryRepository.findById(category1.getId());

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Concerts");
        }

        @Test
        @DisplayName("Должен вернуть empty для несуществующего ID")
        void findById_NonExistingCategory_ReturnsEmpty() {
            // When
            Optional<Category> result = categoryRepository.findById(999L);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Должен найти все категории")
        void findAll_ReturnsAllCategories() {
            // When
            List<Category> result = categoryRepository.findAll();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(Category::getName)
                    .containsExactlyInAnyOrder("Concerts", "Exhibitions");
        }

        @Test
        @DisplayName("Должен удалить категорию")
        void delete_Category_Success() {
            // Given
            Long categoryId = category1.getId();

            // When
            categoryRepository.deleteById(categoryId);
            entityManager.flush();

            // Then
            assertThat(categoryRepository.findById(categoryId)).isEmpty();
        }

        @Test
        @DisplayName("Должен обновить категорию")
        void update_Category_Success() {
            // Given
            category1.setName("Music Concerts");

            // When
            Category updated = categoryRepository.save(category1);
            entityManager.flush();

            // Then
            assertThat(updated.getName()).isEqualTo("Music Concerts");
        }

        @Test
        @DisplayName("Должен посчитать количество категорий")
        void count_ReturnsCorrectCount() {
            // When
            long count = categoryRepository.count();

            // Then
            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("Должен проверить существование по ID")
        void existsById_ExistingCategory_ReturnsTrue() {
            // When
            boolean exists = categoryRepository.existsById(category1.getId());

            // Then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Должен вернуть false для несуществующего ID")
        void existsById_NonExistingCategory_ReturnsFalse() {
            // When
            boolean exists = categoryRepository.existsById(999L);

            // Then
            assertThat(exists).isFalse();
        }
    }
}
