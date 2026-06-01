package ru.practicum.main.category.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Validation Tests")
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Должен выбросить NotFoundException при удалении несуществующей категории")
    void delete_NotFound_ThrowsException() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.delete(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Категория с id=1 не найдена");

        verify(eventRepository, never()).existsByCategoryId(1L);
        verify(categoryRepository, never()).deleteById(1L);
    }

    @Test
    @DisplayName("Должен выбросить ConflictException при удалении категории с событиями")
    void delete_CategoryHasEvents_ThrowsException() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(eventRepository.existsByCategoryId(1L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.delete(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Категория с id=1 используется в событиях");

        verify(categoryRepository, never()).deleteById(1L);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("Должен выбросить ValidationException при некорректном size")
    void getAll_InvalidSize_ThrowsException(int size) {
        assertThatThrownBy(() -> categoryService.getAll(0, size))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("size must be");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -5})
    @DisplayName("Должен выбросить ValidationException при некорректном from")
    void getAll_InvalidFrom_ThrowsException(int from) {
        assertThatThrownBy(() -> categoryService.getAll(from, 10))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("from must be");
    }
}
