package ru.practicum.main.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.main.comment.model.Comment;
import ru.practicum.main.comment.status.CommentStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository for comment persistence and filtering.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Finds comments by author.
     */
    Page<Comment> findAllByAuthorId(Long authorId, Pageable pageable);

    /**
     * Finds comments by author and event.
     */
    Page<Comment> findAllByAuthorIdAndEventId(Long authorId, Long eventId, Pageable pageable);

    @Query("""
    SELECT c FROM Comment c 
    WHERE c.author.id = :userId 
    AND (:eventId IS NULL OR c.event.id = :eventId)
    """)
    Page<Comment> findAllByAuthorIdAndOptionalEventId(
            @Param("userId") Long userId,
            @Param("eventId") Long eventId,
            Pageable pageable);
    /**
     * Finds event comments by status.
     */
    Page<Comment> findAllByEventIdAndStatus(Long eventId, CommentStatus status, Pageable pageable);

    /**
     * Counts user comments for an event excluding the specified status.
     */
    long countByAuthorIdAndEventIdAndStatusNot(Long authorId, Long eventId, CommentStatus excludedStatus);

    /**
     * Finds a single event comment by ID and status.
     */
    Optional<Comment> findByIdAndEventIdAndStatus(Long id, Long eventId, CommentStatus status);

    /**
     * Searches comments for admin with optional filters.
     */
    @Query("SELECT c FROM Comment c " +
            "WHERE (:users IS NULL OR c.author.id IN :users) " +
            "AND (:events IS NULL OR c.event.id IN :events) " +
            "AND (:statuses IS NULL OR c.status IN :statuses)")
    Page<Comment> searchForAdmin(
            @Param("users") List<Long> users,
            @Param("events") List<Long> events,
            @Param("statuses") List<CommentStatus> statuses,
            Pageable pageable);
}
