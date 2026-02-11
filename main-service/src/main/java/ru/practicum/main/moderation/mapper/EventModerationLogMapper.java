package ru.practicum.main.moderation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.moderation.dto.EventModerationLogDto;
import ru.practicum.main.moderation.model.EventModerationLog;

import java.util.List;

/**
 * Mapper for moderation history.
 */
@Mapper(componentModel = "spring")
public interface EventModerationLogMapper {

    /**
     * Converts entity to DTO.
     */
    @Mapping(target = "eventId", source = "event.id")
    EventModerationLogDto toDto(EventModerationLog log);

    /**
     * Converts entities to DTO list.
     */
    List<EventModerationLogDto> toDtoList(List<EventModerationLog> logs);
}
