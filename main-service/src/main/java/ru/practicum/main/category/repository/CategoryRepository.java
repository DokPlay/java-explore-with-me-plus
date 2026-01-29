package ru.practicum.main.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.category.model.Category;

/**
 * Репозиторий для работы с категориями.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Проверить существование категории по имени.
     */
    boolean existsByName(String name);
}
