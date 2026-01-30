package ru.practicum.main.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.model.ParticipationRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    ParticipationRequestDto toDto(ParticipationRequest request);

    List<ParticipationRequestDto> toDtoList(List<ParticipationRequest> requests);
}
