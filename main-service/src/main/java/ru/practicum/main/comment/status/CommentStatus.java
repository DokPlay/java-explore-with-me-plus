package ru.practicum.main.comment.status;

/**
 * Comment lifecycle status.
 */
public enum CommentStatus {
    /**
     * Awaiting moderation.
     */
    PENDING,

    /**
     * Visible in public API.
     */
    PUBLISHED,

    /**
     * Rejected by moderator.
     */
    REJECTED,

    /**
     * Soft-deleted by author.
     */
    DELETED
}
