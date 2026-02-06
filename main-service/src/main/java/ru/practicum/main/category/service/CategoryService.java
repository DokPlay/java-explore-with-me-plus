package ru.practicum.main.category.service;

import ru.practicum.main.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto create(CategoryDto dto);

    CategoryDto update(Long catId, CategoryDto dto);

    void delete(Long catId);

    CategoryDto getById(Long catId);

    List<CategoryDto> getAll(int from, int size);
}
