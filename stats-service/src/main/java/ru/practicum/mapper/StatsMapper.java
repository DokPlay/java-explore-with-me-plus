package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface StatsMapper {
    @Mapping(target = "id", ignore = true)
    EndpointHit toEntity(EndpointHitDto dto);
    
    EndpointHitDto toDto(EndpointHit entity);
}