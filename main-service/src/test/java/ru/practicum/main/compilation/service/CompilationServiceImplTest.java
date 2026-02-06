package ru.practicum.main.compilation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.repository.CompilationRepository;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompilationService Validation Tests")
class CompilationServiceImplTest {

    @Mock
    private CompilationRepository compilationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private CompilationServiceImpl compilationService;

    @Test
    @DisplayName("Должен выбросить NotFoundException при создании подборки с несуществующими событиями")
    void create_MissingEventIds_ThrowsException() {
        NewCompilationDto dto = NewCompilationDto.builder()
                .title("Test")
                .events(List.of(1L, 2L))
                .build();

        Event event = new Event();
        event.setId(1L);
        when(eventRepository.findAllById(any())).thenReturn(List.of(event));

        assertThatThrownBy(() -> compilationService.create(dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("События не найдены");

        verify(compilationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен выбросить ValidationException при создании подборки с null eventId")
    void create_NullEventId_ThrowsException() {
        NewCompilationDto dto = NewCompilationDto.builder()
                .title("Test")
                .events(Arrays.asList(1L, null))
                .build();

        assertThatThrownBy(() -> compilationService.create(dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("eventIds must not contain null");
    }

    @Test
    @DisplayName("Должен выбросить NotFoundException при обновлении подборки с несуществующими событиями")
    void update_MissingEventIds_ThrowsException() {
        Compilation compilation = new Compilation();
        compilation.setId(1L);
        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation));

        UpdateCompilationRequest dto = UpdateCompilationRequest.builder()
                .events(List.of(1L, 2L))
                .build();

        Event event = new Event();
        event.setId(1L);
        when(eventRepository.findAllById(any())).thenReturn(List.of(event));

        assertThatThrownBy(() -> compilationService.update(1L, dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("События не найдены");

        verify(compilationRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("Должен выбросить ValidationException при некорректном size")
    void getAll_InvalidSize_ThrowsException(int size) {
        assertThatThrownBy(() -> compilationService.getAll(null, 0, size))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("size must be");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -10})
    @DisplayName("Должен выбросить ValidationException при некорректном from")
    void getAll_InvalidFrom_ThrowsException(int from) {
        assertThatThrownBy(() -> compilationService.getAll(null, from, 10))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("from must be");
    }
}
