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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.comment.dto.AdminUpdateCommentRequest;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.service.CommentService;
import ru.practicum.main.comment.status.CommentStatus;

import java.util.List;

/**
 * Administrative API for comment moderation.
 */
@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCommentController {

    private final CommentService commentService;

    /**
     * Returns comments using admin filters.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getComments(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<Long> events,
            @RequestParam(required = false) List<CommentStatus> statuses,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET /admin/comments - Получение комментариев для администратора");
        return commentService.getCommentsForAdmin(users, events, statuses, from, size);
    }

    /**
     * Moderates one comment.
     */
    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto moderateComment(
            @PathVariable @Positive Long commentId,
            @Valid @RequestBody AdminUpdateCommentRequest request) {
        log.info("PATCH /admin/comments/{} - Модерация комментария", commentId);
        return commentService.moderateComment(commentId, request);
    }

    /**
     * Hard deletes comment.
     */
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long commentId) {
        log.info("DELETE /admin/comments/{} - Удаление комментария", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }
}
