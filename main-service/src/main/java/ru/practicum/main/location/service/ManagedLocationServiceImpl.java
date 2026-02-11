package ru.practicum.main.location.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.location.dto.ManagedLocationDto;
import ru.practicum.main.location.dto.NewManagedLocationDto;
import ru.practicum.main.location.dto.UpdateManagedLocationDto;
import ru.practicum.main.location.mapper.ManagedLocationMapper;
import ru.practicum.main.location.model.ManagedLocation;
import ru.practicum.main.location.repository.ManagedLocationRepository;
import ru.practicum.main.util.PaginationValidator;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Managed location service implementation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ManagedLocationServiceImpl implements ManagedLocationService {

    private final ManagedLocationRepository managedLocationRepository;
    private final ManagedLocationMapper managedLocationMapper;

    @Override
    @Transactional
    public ManagedLocationDto createLocation(NewManagedLocationDto dto) {
        log.info("Создание управляемой локации name={}", dto.getName());
        if (managedLocationRepository.existsByNameIgnoreCase(dto.getName().trim())) {
            throw new ConflictException("Локация с таким названием уже существует");
        }

        ManagedLocation location = ManagedLocation.builder()
                .name(dto.getName().trim())
                .lat(dto.getLat())
                .lon(dto.getLon())
                .active(true)
                .createdOn(nowTruncatedToMillis())
                .build();

        ManagedLocation saved = managedLocationRepository.save(location);
        return managedLocationMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ManagedLocationDto updateLocation(Long locationId, UpdateManagedLocationDto dto) {
        log.info("Обновление управляемой локации locationId={}", locationId);
        ManagedLocation location = managedLocationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Локация не найдена: id=" + locationId));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            String normalizedName = dto.getName().trim();
            if (!location.getName().equalsIgnoreCase(normalizedName)
                    && managedLocationRepository.existsByNameIgnoreCase(normalizedName)) {
                throw new ConflictException("Локация с таким названием уже существует");
            }
            location.setName(normalizedName);
        }
        if (dto.getLat() != null) {
            location.setLat(dto.getLat());
        }
        if (dto.getLon() != null) {
            location.setLon(dto.getLon());
        }
        if (dto.getActive() != null) {
            location.setActive(dto.getActive());
        }
        location.setUpdatedOn(nowTruncatedToMillis());

        ManagedLocation updated = managedLocationRepository.save(location);
        return managedLocationMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteLocation(Long locationId) {
        log.info("Удаление управляемой локации locationId={}", locationId);
        ManagedLocation location = managedLocationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Локация не найдена: id=" + locationId));

        if (!location.getActive()) {
            return;
        }

        location.setActive(false);
        location.setUpdatedOn(nowTruncatedToMillis());
        managedLocationRepository.save(location);
    }

    @Override
    public ManagedLocationDto getLocationById(Long locationId) {
        ManagedLocation location = managedLocationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Локация не найдена: id=" + locationId));
        return managedLocationMapper.toDto(location);
    }

    @Override
    public List<ManagedLocationDto> getLocations(Boolean active, int from, int size) {
        PaginationValidator.validatePagination(from, size);

        Pageable pageable = PageRequest.of(
                from / size,
                size,
                Sort.by("createdOn").descending().and(Sort.by("id").descending())
        );

        List<ManagedLocation> locations = active == null
                ? managedLocationRepository.findAll(pageable).getContent()
                : managedLocationRepository.findAllByActive(active, pageable).getContent();

        return managedLocationMapper.toDtoList(locations);
    }

    @Override
    public ManagedLocationDto getPublicLocationById(Long locationId) {
        ManagedLocation location = managedLocationRepository.findByIdAndActiveTrue(locationId)
                .orElseThrow(() -> new NotFoundException("Активная локация не найдена: id=" + locationId));
        return managedLocationMapper.toDto(location);
    }

    @Override
    public List<ManagedLocationDto> getPublicLocations(int from, int size) {
        return getLocations(true, from, size);
    }

    private LocalDateTime nowTruncatedToMillis() {
        return LocalDateTime.now()
                .truncatedTo(ChronoUnit.MILLIS);
    }
}
