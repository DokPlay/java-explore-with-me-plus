package ru.practicum.main.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentDto;
import ru.practicum.main.comment.service.CommentService;

import java.util.List;

/**
 * Private API for comment management by author.
 */
@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateCommentController {

    private final CommentService commentService;

    /**
     * Creates a new comment for an event.
     */
    @PostMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody NewCommentDto dto) {
        log.info("POST /users/{}/events/{}/comments - Создание комментария", userId, eventId);
        return commentService.createComment(userId, eventId, dto);
    }

    /**
     * Returns comments authored by the user.
     */
    @GetMapping("/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getUserComments(
            @PathVariable @Positive Long userId,
            @RequestParam(required = false) @Positive Long eventId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET /users/{}/comments - Получение комментариев пользователя", userId);
        return commentService.getUserComments(userId, eventId, from, size);
    }

    /**
     * Updates a user-owned comment.
     */
    @PatchMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long commentId,
            @Valid @RequestBody UpdateCommentDto dto) {
        log.info("PATCH /users/{}/comments/{} - Обновление комментария", userId, commentId);
        return commentService.updateCommentByAuthor(userId, commentId, dto);
    }

    /**
     * Soft deletes a user-owned comment.
     */
    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long commentId) {
        log.info("DELETE /users/{}/comments/{} - Удаление комментария", userId, commentId);
        commentService.deleteCommentByAuthor(userId, commentId);
    }
}
