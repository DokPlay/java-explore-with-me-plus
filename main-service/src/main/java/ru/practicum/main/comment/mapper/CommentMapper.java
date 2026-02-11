package ru.practicum.main.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.model.Comment;

import java.util.List;

/**
 * MapStruct mapper for comment entity and DTO.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper {

    /**
     * Converts entity to API DTO.
     */
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "authorId", source = "author.id")
    CommentDto toDto(Comment comment);

    /**
     * Converts list of entities to API DTO list.
     */
    List<CommentDto> toDtoList(List<Comment> comments);
}
