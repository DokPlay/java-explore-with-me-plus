package ru.practicum.main.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.LocationDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.user.dto.UserShortDto;
import ru.practicum.main.user.model.User;

import java.util.List;

/**
 * Маппер для преобразования Event сущностей в DTO и обратно.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    /**
     * Преобразует NewEventDto в Event.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    Event toEvent(NewEventDto dto);

    /**
     * Преобразует Event в EventFullDto.
     */
    EventFullDto toEventFullDto(Event event);

    /**
     * Преобразует Event в EventShortDto.
     */
    EventShortDto toEventShortDto(Event event);

    /**
     * Преобразует список Event в список EventShortDto.
     */
    List<EventShortDto> toEventShortDtoList(List<Event> events);

    /**
     * Преобразует список Event в список EventFullDto.
     */
    List<EventFullDto> toEventFullDtoList(List<Event> events);

    /**
     * Обновляет Event из UpdateEventUserRequest.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    void updateEventFromUserRequest(UpdateEventUserRequest dto, @MappingTarget Event event);

    /**
     * Обновляет Event из UpdateEventAdminRequest.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    void updateEventFromAdminRequest(UpdateEventAdminRequest dto, @MappingTarget Event event);

    /**
     * Преобразует Category в CategoryDto.
     */
    CategoryDto toCategoryDto(Category category);

    /**
     * Преобразует User в UserShortDto.
     */
    UserShortDto toUserShortDto(User user);

    /**
     * Преобразует Location в LocationDto.
     */
    LocationDto toLocationDto(Location location);

    /**
     * Преобразует LocationDto в Location.
     */
    Location toLocation(LocationDto dto);
}
