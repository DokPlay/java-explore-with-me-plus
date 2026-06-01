package ru.practicum.main.location.service;

import ru.practicum.main.location.dto.ManagedLocationDto;
import ru.practicum.main.location.dto.NewManagedLocationDto;
import ru.practicum.main.location.dto.UpdateManagedLocationDto;

import java.util.List;

/**
 * Business operations for managed locations.
 */
public interface ManagedLocationService {

    /**
     * Creates new managed location.
     */
    ManagedLocationDto createLocation(NewManagedLocationDto dto);

    /**
     * Updates managed location.
     */
    ManagedLocationDto updateLocation(Long locationId, UpdateManagedLocationDto dto);

    /**
     * Soft-deletes managed location.
     */
    void deleteLocation(Long locationId);

    /**
     * Returns location by id for admin.
     */
    ManagedLocationDto getLocationById(Long locationId);

    /**
     * Returns location list for admin.
     */
    List<ManagedLocationDto> getLocations(Boolean active, int from, int size);

    /**
     * Returns public active location by id.
     */
    ManagedLocationDto getPublicLocationById(Long locationId);

    /**
     * Returns public active locations.
     */
    List<ManagedLocationDto> getPublicLocations(int from, int size);
}
