package ru.practicum.main.location.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.location.dto.ManagedLocationDto;
import ru.practicum.main.location.service.ManagedLocationService;

import java.util.List;

/**
 * Public API for active managed locations.
 */
@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicLocationController {

    private final ManagedLocationService managedLocationService;

    /**
     * Returns active locations.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ManagedLocationDto> getLocations(
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET /locations - Получение активных локаций");
        return managedLocationService.getPublicLocations(from, size);
    }

    /**
     * Returns active location by id.
     */
    @GetMapping("/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public ManagedLocationDto getLocationById(@PathVariable @Positive Long locationId) {
        log.info("GET /locations/{} - Получение активной локации", locationId);
        return managedLocationService.getPublicLocationById(locationId);
    }
}
