package ru.practicum.main.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    @Override
    public CategoryDto create(CategoryDto dto) {
        if (repository.existsByName(dto.getName())) {
            throw new ConflictException("Категория с именем '" + dto.getName() + "' уже существует");
        }
        return toDto(repository.save(Category.builder().name(dto.getName()).build()));
    }

    @Override
    public CategoryDto update(Long catId, CategoryDto dto) {
        Category category = repository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + catId + " не найдена"));

        if (!category.getName().equals(dto.getName()) && repository.existsByName(dto.getName())) {
            throw new ConflictException("Категория с именем '" + dto.getName() + "' уже существует");
        }

        category.setName(dto.getName());
        return toDto(repository.save(category));
    }

    @Override
    public void delete(Long catId) {
        repository.deleteById(catId);
    }

    @Override
    public CategoryDto getById(Long catId) {
        return toDto(repository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + catId + " не найдена")));
    }

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        return repository.findAll(PageRequest.of(from / size, size))
                .stream().map(this::toDto).toList();
    }

    private CategoryDto toDto(Category c) {
        return CategoryDto.builder()
                .id(c.getId())
                .name(c.getName())
                .build();
    }
}
