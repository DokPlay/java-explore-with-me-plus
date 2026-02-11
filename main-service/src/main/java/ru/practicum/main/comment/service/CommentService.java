package ru.practicum.main.comment.service;

import ru.practicum.main.comment.dto.AdminUpdateCommentRequest;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentDto;
import ru.practicum.main.comment.status.CommentStatus;

import java.util.List;

/**
 * Business service for event comments.
 */
public interface CommentService {

    /**
     * Creates a new comment for the event by the user.
     */
    CommentDto createComment(Long userId, Long eventId, NewCommentDto dto);

    /**
     * Updates a user-owned comment.
     */
    CommentDto updateCommentByAuthor(Long userId, Long commentId, UpdateCommentDto dto);

    /**
     * Performs soft delete of a user-owned comment.
     */
    void deleteCommentByAuthor(Long userId, Long commentId);

    /**
     * Returns comments created by a specific user.
     */
    List<CommentDto> getUserComments(Long userId, Long eventId, int from, int size);

    /**
     * Returns published comments for a specific event.
     */
    List<CommentDto> getPublishedComments(Long eventId, int from, int size);

    /**
     * Returns a published comment by event and comment ID.
     */
    CommentDto getPublishedCommentById(Long eventId, Long commentId);

    /**
     * Returns comments for administrators with filters.
     */
    List<CommentDto> getCommentsForAdmin(List<Long> users, List<Long> events, List<CommentStatus> statuses,
                                         int from, int size);

    /**
     * Moderates a comment (publish/reject).
     */
    CommentDto moderateComment(Long commentId, AdminUpdateCommentRequest request);

    /**
     * Hard deletes comment by administrator.
     */
    void deleteCommentByAdmin(Long commentId);
}
