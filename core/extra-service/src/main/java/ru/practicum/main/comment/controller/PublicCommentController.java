package ru.practicum.main.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.service.CommentService;

import java.util.List;

/**
 * Public API for reading event comments.
 */
@RestController
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCommentController {

    private final CommentService commentService;

    /**
     * Returns published comments for an event.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getPublishedComments(
            @PathVariable @Positive Long eventId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET /events/{}/comments - Получение опубликованных комментариев", eventId);
        return commentService.getPublishedComments(eventId, from, size);
    }

    /**
     * Returns a published comment by ID.
     */
    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getPublishedCommentById(
            @PathVariable @Positive Long eventId,
            @PathVariable @Positive Long commentId) {
        log.info("GET /events/{}/comments/{} - Получение опубликованного комментария", eventId, commentId);
        return commentService.getPublishedCommentById(eventId, commentId);
    }
}
