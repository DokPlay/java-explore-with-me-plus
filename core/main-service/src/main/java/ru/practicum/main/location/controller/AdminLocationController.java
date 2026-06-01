package ru.practicum.main.location.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.location.dto.ManagedLocationDto;
import ru.practicum.main.location.dto.NewManagedLocationDto;
import ru.practicum.main.location.dto.UpdateManagedLocationDto;
import ru.practicum.main.location.service.ManagedLocationService;

import java.util.List;

/**
 * Administrative API for managed locations.
 */
@RestController
@RequestMapping("/admin/locations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminLocationController {

    private final ManagedLocationService managedLocationService;

    /**
     * Creates managed location.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ManagedLocationDto createLocation(@Valid @RequestBody NewManagedLocationDto dto) {
        log.info("POST /admin/locations - Создание локации");
        return managedLocationService.createLocation(dto);
    }

    /**
     * Updates managed location.
     */
    @PatchMapping("/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public ManagedLocationDto updateLocation(
            @PathVariable @Positive Long locationId,
            @Valid @RequestBody UpdateManagedLocationDto dto) {
        log.info("PATCH /admin/locations/{} - Обновление локации", locationId);
        return managedLocationService.updateLocation(locationId, dto);
    }

    /**
     * Soft-deletes location.
     */
    @DeleteMapping("/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocation(@PathVariable @Positive Long locationId) {
        log.info("DELETE /admin/locations/{} - Удаление локации", locationId);
        managedLocationService.deleteLocation(locationId);
    }

    /**
     * Returns location by id for admin.
     */
    @GetMapping("/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public ManagedLocationDto getLocationById(@PathVariable @Positive Long locationId) {
        log.info("GET /admin/locations/{} - Получение локации", locationId);
        return managedLocationService.getLocationById(locationId);
    }

    /**
     * Returns managed locations with optional active filter.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ManagedLocationDto> getLocations(
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET /admin/locations - Получение списка локаций");
        return managedLocationService.getLocations(active, from, size);
    }
}
