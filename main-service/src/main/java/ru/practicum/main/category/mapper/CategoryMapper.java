package ru.practicum.main.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.model.Category;

/**
 * Mapper for converting categories between entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * Converts category entity to DTO.
     */
    CategoryDto toDto(Category category);

    /**
     * Converts category DTO to entity.
     */
    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryDto dto);
}
