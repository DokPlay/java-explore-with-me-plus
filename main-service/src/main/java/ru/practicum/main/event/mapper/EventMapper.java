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
 * Mapper for converting events between entities and DTOs.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    /**
     * Converts {@link NewEventDto} to {@link Event}.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "moderationNote", ignore = true)
    @Mapping(target = "location", source = "location")
    Event toEvent(NewEventDto dto);

    /**
     * Converts {@link Event} to {@link EventFullDto}.
     */
    EventFullDto toEventFullDto(Event event);

    /**
     * Converts {@link Event} to {@link EventShortDto}.
     */
    EventShortDto toEventShortDto(Event event);

    /**
     * Converts a list of {@link Event} to a list of {@link EventShortDto}.
     */
    List<EventShortDto> toEventShortDtoList(List<Event> events);

    /**
     * Converts a list of {@link Event} to a list of {@link EventFullDto}.
     */
    List<EventFullDto> toEventFullDtoList(List<Event> events);

    /**
     * Updates {@link Event} with data from {@link UpdateEventUserRequest}.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "moderationNote", ignore = true)
    @Mapping(target = "location", source = "location")
    void updateEventFromUserRequest(UpdateEventUserRequest dto, @MappingTarget Event event);

    /**
     * Updates {@link Event} with data from {@link UpdateEventAdminRequest}.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "location", source = "location")
    void updateEventFromAdminRequest(UpdateEventAdminRequest dto, @MappingTarget Event event);

    /**
     * Converts {@link Category} to {@link CategoryDto}.
     */
    CategoryDto toCategoryDto(Category category);

    /**
     * Converts {@link User} to {@link UserShortDto}.
     */
    UserShortDto toUserShortDto(User user);

    /**
     * Converts {@link Location} to {@link LocationDto}.
     */
    LocationDto toLocationDto(Location location);

    /**
     * Converts {@link LocationDto} to {@link Location}.
     */
    Location toLocation(LocationDto dto);
}
