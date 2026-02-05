package ru.practicum.main.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.util.PaginationValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto create(CategoryDto dto) {
        log.info("Создание категории с именем: {}", dto.getName());

        if (repository.existsByName(dto.getName())) {
            log.warn("Попытка создать категорию с уже существующим именем: {}", dto.getName());
            throw new ConflictException("Категория с именем '" + dto.getName() + "' уже существует");
        }

        Category saved = repository.save(Category.builder().name(dto.getName()).build());
        log.info("Категория создана успешно с id: {}", saved.getId());
        return toDto(saved);
    }

    @Override
    public CategoryDto update(Long catId, CategoryDto dto) {
        log.info("Обновление категории с id: {}, новое имя: {}", catId, dto.getName());

        Category category = repository.findById(catId)
                .orElseThrow(() -> {
                    log.warn("Категория с id={} не найдена", catId);
                    return new NotFoundException("Категория с id=" + catId + " не найдена");
                });

        if (!category.getName().equals(dto.getName()) && repository.existsByName(dto.getName())) {
            log.warn("Попытка обновить категорию на уже существующее имя: {}", dto.getName());
            throw new ConflictException("Категория с именем '" + dto.getName() + "' уже существует");
        }

        category.setName(dto.getName());
        Category updated = repository.save(category);
        log.info("Категория с id={} успешно обновлена", catId);
        return toDto(updated);
    }

    @Override
    public void delete(Long catId) {
        log.info("Удаление категории с id: {}", catId);

        if (!repository.existsById(catId)) {
            log.warn("Категория с id={} не найдена", catId);
            throw new NotFoundException("Категория с id=" + catId + " не найдена");
        }

        if (eventRepository.existsByCategoryId(catId)) {
            log.warn("Категория с id={} не может быть удалена, так как используется в событиях", catId);
            throw new ConflictException("Категория с id=" + catId + " используется в событиях");
        }

        repository.deleteById(catId);
        log.info("Категория с id={} успешно удалена", catId);
    }

    @Override
    public CategoryDto getById(Long catId) {
        log.info("Получение категории по id: {}", catId);

        return toDto(repository.findById(catId)
                .orElseThrow(() -> {
                    log.warn("Категория с id={} не найдена", catId);
                    return new NotFoundException("Категория с id=" + catId + " не найдена");
                }));
    }

    @Override
    public List<CategoryDto> getAll(int from, int size) {
        log.info("Получение всех категорий с from={}, size={}", from, size);

        PaginationValidator.validatePagination(from, size);
        List<CategoryDto> result = repository.findAll(PageRequest.of(from / size, size))
                .stream().map(this::toDto).toList();

        log.info("Найдено {} категорий", result.size());
        return result;
    }

    private CategoryDto toDto(Category c) {
        return CategoryDto.builder()
                .id(c.getId())
                .name(c.getName())
                .build();
    }
}