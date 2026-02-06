package ru.practicum.main.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.repository.CompilationRepository;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.util.PaginationValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto dto) {
        Set<Event> events = getEventsByIdsOrThrow(dto.getEvents());

        Boolean pinned = dto.getPinned() != null ? dto.getPinned() : false;

        Compilation compilation = Compilation.builder()
                .title(dto.getTitle())
                .pinned(pinned)
                .events(events)
                .build();

        return toDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Подборка с id=" + compId + " не найдена");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequest dto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id=" + compId + " не найдена"));

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getEvents() != null) {
            compilation.setEvents(getEventsByIdsOrThrow(dto.getEvents()));
        }

        return toDto(compilationRepository.save(compilation));
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        PaginationValidator.validatePagination(from, size);
        PageRequest page = PageRequest.of(from / size, size);
        List<Compilation> comps = pinned == null
                ? compilationRepository.findAll(page).getContent()
                : compilationRepository.findAllByPinned(pinned, page);

        return comps.stream().map(this::toDto).toList();
    }

    @Override
    public CompilationDto getById(Long compId) {
        return toDto(compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id=" + compId + " не найдена")));
    }

    private CompilationDto toDto(Compilation compilation) {
        List<EventShortDto> events = compilation.getEvents().stream()
                .map(eventMapper::toEventShortDto)
                .toList();

        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(events)
                .build();
    }

    private Set<Event> getEventsByIdsOrThrow(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Set.of();
        }
        if (eventIds.stream().anyMatch(Objects::isNull)) {
            throw new ValidationException("eventIds must not contain null");
        }

        Set<Long> uniqueIds = new HashSet<>(eventIds);
        List<Event> events = eventRepository.findAllById(uniqueIds);

        if (events.size() != uniqueIds.size()) {
            Set<Long> foundIds = events.stream()
                    .map(Event::getId)
                    .collect(Collectors.toSet());
            List<Long> missingIds = uniqueIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .sorted()
                    .toList();
            throw new NotFoundException("События не найдены: id=" + missingIds);
        }

        return new HashSet<>(events);
    }
}
