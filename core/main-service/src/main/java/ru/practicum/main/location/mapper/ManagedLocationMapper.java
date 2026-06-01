package ru.practicum.main.location.mapper;

import org.mapstruct.Mapper;
import ru.practicum.main.location.dto.ManagedLocationDto;
import ru.practicum.main.location.model.ManagedLocation;

import java.util.List;

/**
 * Mapper for managed locations.
 */
@Mapper(componentModel = "spring")
public interface ManagedLocationMapper {

    /**
     * Converts entity to DTO.
     */
    ManagedLocationDto toDto(ManagedLocation location);

    /**
     * Converts entities to DTO list.
     */
    List<ManagedLocationDto> toDtoList(List<ManagedLocation> locations);
}
