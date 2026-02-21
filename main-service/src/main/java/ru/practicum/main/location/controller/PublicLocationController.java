package ru.practicum.main.location.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.service.EventService;
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
    private final EventService eventService;

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

    /**
     * Returns published events near active managed location center.
     */
    @GetMapping("/{locationId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsNearLocation(
            @PathVariable @Positive Long locationId,
            @RequestParam(required = false) @Positive Double radiusKm,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size,
            HttpServletRequest request) {
        log.info("GET /locations/{}/events - Поиск событий в радиусе {} км", locationId, radiusKm);
        return eventService.searchPublicEventsByLocation(locationId, radiusKm, sort, from, size, request);
    }
}
