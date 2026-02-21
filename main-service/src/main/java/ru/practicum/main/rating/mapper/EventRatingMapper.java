package ru.practicum.main.rating.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.rating.dto.EventVoteDto;
import ru.practicum.main.rating.model.EventRating;

import java.util.List;

/**
 * Mapper for event votes.
 */
@Mapper(componentModel = "spring")
public interface EventRatingMapper {

    /**
     * Converts entity to API DTO.
     */
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "userId", source = "user.id")
    EventVoteDto toDto(EventRating eventRating);

    /**
     * Converts entities to API DTO list.
     */
    List<EventVoteDto> toDtoList(List<EventRating> votes);
}
