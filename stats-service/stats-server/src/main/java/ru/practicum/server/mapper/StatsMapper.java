package ru.practicum.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.server.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface StatsMapper {

    @Mapping(target = "id", ignore = true)
    EndpointHit toEntity(EndpointHitDto endpointHitDto);

    EndpointHitDto toDto(EndpointHit endpointHit);
}