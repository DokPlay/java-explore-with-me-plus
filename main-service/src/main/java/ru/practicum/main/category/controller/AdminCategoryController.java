package ru.practicum.main.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.service.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
@Slf4j
public class AdminCategoryController {

    private final CategoryService service;

    @PostMapping
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody CategoryDto dto) {
        log.info("Admin: Запрос на создание новой категории с именем: {}", dto.getName());
        CategoryDto created = service.create(dto);
        log.info("Admin: Категория успешно создана с id: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@PathVariable Long catId,
                              @Valid @RequestBody CategoryDto dto) {
        log.info("Admin: Запрос на обновление категории с id: {}, новое имя: {}", catId, dto.getName());
        CategoryDto updated = service.update(catId, dto);
        log.info("Admin: Категория с id={} успешно обновлена", catId);
        return updated;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        log.info("Admin: Запрос на удаление категории с id: {}", catId);
        service.delete(catId);
        log.info("Admin: Категория с id={} успешно удалена", catId);
    }
}