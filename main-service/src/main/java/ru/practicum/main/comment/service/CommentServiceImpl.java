package ru.practicum.main.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.comment.dto.AdminUpdateCommentRequest;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentDto;
import ru.practicum.main.comment.mapper.CommentMapper;
import ru.practicum.main.comment.model.Comment;
import ru.practicum.main.comment.repository.CommentRepository;
import ru.practicum.main.comment.status.CommentStatus;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;
import ru.practicum.main.util.PaginationValidator;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Comment service implementation with moderation workflow.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    /**
     * Placeholder content for soft-deleted comments.
     * TODO: Move this value to a localization resource bundle.
     */
    private static final String SOFT_DELETED_TEXT = "[удалено автором]";

    private static final String DEFAULT_REJECTION_NOTE = "Отклонено администратором";

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto dto) {
        log.info("Создание комментария: userId={}, eventId={}", userId, eventId);
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + eventId));

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Комментировать можно только опубликованные события");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может комментировать своё событие");
        }

        Comment comment = Comment.builder()
                .text(dto.getText().trim())
                .event(event)
                .author(author)
                .createdOn(nowTruncatedToMillis())
                .status(CommentStatus.PENDING)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    @Transactional
    public CommentDto updateCommentByAuthor(Long userId, Long commentId, UpdateCommentDto dto) {
        log.info("Обновление комментария автором: userId={}, commentId={}", userId, commentId);
        Comment comment = getCommentOrThrow(commentId);
        validateCommentOwner(comment, userId);

        if (comment.getStatus() == CommentStatus.DELETED) {
            throw new ConflictException("Удалённый комментарий нельзя редактировать");
        }

        comment.setText(dto.getText().trim());
        comment.setUpdatedOn(nowTruncatedToMillis());
        comment.setStatus(CommentStatus.PENDING);
        comment.setModerationNote(null);

        // TODO: Send async notification to moderators after user edit.
        Comment updatedComment = commentRepository.save(comment);
        return commentMapper.toDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteCommentByAuthor(Long userId, Long commentId) {
        log.info("Удаление комментария автором: userId={}, commentId={}", userId, commentId);
        Comment comment = getCommentOrThrow(commentId);
        validateCommentOwner(comment, userId);

        if (comment.getStatus() == CommentStatus.DELETED) {
            return;
        }

        comment.setText(SOFT_DELETED_TEXT);
        comment.setStatus(CommentStatus.DELETED);
        comment.setUpdatedOn(nowTruncatedToMillis());
        comment.setModerationNote("Удалено автором");
        commentRepository.save(comment);
    }

    @Override
    public List<CommentDto> getUserComments(Long userId, Long eventId, int from, int size) {
        log.info("Получение комментариев пользователя: userId={}, eventId={}", userId, eventId);
        PaginationValidator.validatePagination(from, size);
        validateUserExists(userId);

        Pageable pageable = PageRequest.of(
                from / size,
                size,
                Sort.by("createdOn").descending().and(Sort.by("id").descending())
        );

        Page<Comment> commentsPage = eventId == null
                ? commentRepository.findAllByAuthorId(userId, pageable)
                : commentRepository.findAllByAuthorIdAndEventId(userId, eventId, pageable);

        return commentMapper.toDtoList(commentsPage.getContent());
    }

    @Override
    public List<CommentDto> getPublishedComments(Long eventId, int from, int size) {
        log.info("Получение опубликованных комментариев события: eventId={}", eventId);
        PaginationValidator.validatePagination(from, size);
        validateEventPublished(eventId);

        Pageable pageable = PageRequest.of(
                from / size,
                size,
                Sort.by("createdOn").descending().and(Sort.by("id").descending())
        );
        List<Comment> comments = commentRepository
                .findAllByEventIdAndStatus(eventId, CommentStatus.PUBLISHED, pageable)
                .getContent();

        return commentMapper.toDtoList(comments);
    }

    @Override
    public CommentDto getPublishedCommentById(Long eventId, Long commentId) {
        log.info("Получение опубликованного комментария: eventId={}, commentId={}", eventId, commentId);
        validateEventPublished(eventId);

        Comment comment = commentRepository
                .findByIdAndEventIdAndStatus(commentId, eventId, CommentStatus.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден: id=" + commentId));

        return commentMapper.toDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsForAdmin(List<Long> users, List<Long> events, List<CommentStatus> statuses,
                                                int from, int size) {
        log.info("Получение комментариев для администратора: users={}, events={}, statuses={}",
                users, events, statuses);
        PaginationValidator.validatePagination(from, size);

        Pageable pageable = PageRequest.of(
                from / size,
                size,
                Sort.by("createdOn").descending().and(Sort.by("id").descending())
        );

        List<Comment> comments = commentRepository.searchForAdmin(
                        normalizeFilter(users),
                        normalizeFilter(events),
                        normalizeFilter(statuses),
                        pageable)
                .getContent();
        return commentMapper.toDtoList(comments);
    }

    @Override
    @Transactional
    public CommentDto moderateComment(Long commentId, AdminUpdateCommentRequest request) {
        log.info("Модерация комментария: commentId={}, action={}", commentId, request.getAction());
        Comment comment = getCommentOrThrow(commentId);

        if (comment.getStatus() == CommentStatus.DELETED) {
            throw new ConflictException("Удалённый комментарий нельзя модерировать");
        }

        switch (request.getAction()) {
            case PUBLISH -> {
                // TODO: Add anti-spam/profanity checks before publishing to public feed.
                comment.setStatus(CommentStatus.PUBLISHED);
                comment.setModerationNote(null);
            }
            case REJECT -> {
                comment.setStatus(CommentStatus.REJECTED);
                comment.setModerationNote(hasText(request.getModerationNote())
                        ? request.getModerationNote().trim()
                        : DEFAULT_REJECTION_NOTE);
            }
        }

        comment.setUpdatedOn(nowTruncatedToMillis());
        Comment updatedComment = commentRepository.save(comment);
        return commentMapper.toDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        log.info("Удаление комментария администратором: commentId={}", commentId);
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Комментарий не найден: id=" + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    private Comment getCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден: id=" + commentId));
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден: id=" + userId);
        }
    }

    private void validateEventPublished(Long eventId) {
        eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Опубликованное событие не найдено: id=" + eventId));
    }

    private void validateCommentOwner(Comment comment, Long userId) {
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new NotFoundException("Комментарий не принадлежит пользователю: userId=" + userId);
        }
    }

    private <T> List<T> normalizeFilter(List<T> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private LocalDateTime nowTruncatedToMillis() {
        return LocalDateTime.now()
                .truncatedTo(ChronoUnit.MILLIS);
    }
}
