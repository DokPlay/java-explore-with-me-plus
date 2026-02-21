package ru.practicum.main.comment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CommentServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService Unit Tests")
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private User eventInitiator;
    private Event publishedEvent;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Comment Author")
                .email("author@test.ru")
                .build();

        eventInitiator = User.builder()
                .id(2L)
                .name("Initiator")
                .email("init@test.ru")
                .build();

        publishedEvent = Event.builder()
                .id(10L)
                .title("Published Event")
                .annotation("Published event annotation")
                .description("Published event description")
                .eventDate(LocalDateTime.now().plusDays(3))
                .createdOn(LocalDateTime.now().minusDays(1))
                .state(EventState.PUBLISHED)
                .initiator(eventInitiator)
                .build();

        comment = Comment.builder()
                .id(100L)
                .text("Initial comment text")
                .event(publishedEvent)
                .author(user)
                .createdOn(LocalDateTime.now().minusHours(2))
                .status(CommentStatus.PENDING)
                .build();

        commentDto = CommentDto.builder()
                .id(100L)
                .text("Initial comment text")
                .eventId(10L)
                .authorId(1L)
                .status(CommentStatus.PENDING)
                .build();
    }

    @Nested
    @DisplayName("createComment")
    class CreateCommentTests {

        @Test
        @DisplayName("Должен создать комментарий к опубликованному событию")
        void createComment_success() {
            NewCommentDto request = NewCommentDto.builder()
                    .text(" Новый комментарий ")
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(eventRepository.findById(10L)).thenReturn(Optional.of(publishedEvent));
            when(commentRepository.save(any(Comment.class))).thenReturn(comment);
            when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);

            CommentDto result = commentService.createComment(1L, 10L, request);

            assertThat(result).isNotNull();
            verify(commentRepository).save(any(Comment.class));
        }

        @Test
        @DisplayName("Должен выбросить ConflictException для неопубликованного события")
        void createComment_eventNotPublished_throwsConflict() {
            Event pendingEvent = Event.builder()
                    .id(10L)
                    .state(EventState.PENDING)
                    .initiator(eventInitiator)
                    .build();
            NewCommentDto request = NewCommentDto.builder().text("Текст").build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(eventRepository.findById(10L)).thenReturn(Optional.of(pendingEvent));

            assertThatThrownBy(() -> commentService.createComment(1L, 10L, request))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("опубликованные");
        }

        @Test
        @DisplayName("Должен выбросить ConflictException если инициатор комментирует своё событие")
        void createComment_initiatorCommentOwnEvent_throwsConflict() {
            User sameUser = User.builder().id(1L).name("User").email("user@test.ru").build();
            Event ownEvent = Event.builder()
                    .id(11L)
                    .state(EventState.PUBLISHED)
                    .initiator(sameUser)
                    .build();
            NewCommentDto request = NewCommentDto.builder().text("Текст").build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(sameUser));
            when(eventRepository.findById(11L)).thenReturn(Optional.of(ownEvent));

            assertThatThrownBy(() -> commentService.createComment(1L, 11L, request))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("Инициатор события");
        }

        @Test
        @DisplayName("Должен выбросить ConflictException при превышении лимита комментариев")
        void createComment_limitExceeded_throwsConflict() {
            NewCommentDto request = NewCommentDto.builder().text("Новый комментарий").build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(eventRepository.findById(10L)).thenReturn(Optional.of(publishedEvent));
            when(commentRepository.countByAuthorIdAndEventIdAndStatusNot(1L, 10L, CommentStatus.DELETED))
                    .thenReturn(5L);

            assertThatThrownBy(() -> commentService.createComment(1L, 10L, request))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("Превышен лимит комментариев");
        }
    }

    @Nested
    @DisplayName("updateCommentByAuthor")
    class UpdateCommentByAuthorTests {

        @Test
        @DisplayName("Должен обновить текст и вернуть комментарий в статус PENDING")
        void updateCommentByAuthor_success() {
            UpdateCommentDto request = UpdateCommentDto.builder()
                    .text("Обновлённый текст")
                    .build();
            comment.setStatus(CommentStatus.REJECTED);
            comment.setModerationNote("Слишком коротко");

            when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));
            when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);

            commentService.updateCommentByAuthor(1L, 100L, request);

            ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
            verify(commentRepository).save(captor.capture());
            Comment saved = captor.getValue();

            assertThat(saved.getText()).isEqualTo("Обновлённый текст");
            assertThat(saved.getStatus()).isEqualTo(CommentStatus.PENDING);
            assertThat(saved.getModerationNote()).isNull();
            assertThat(saved.getUpdatedOn()).isNotNull();
        }

        @Test
        @DisplayName("Должен выбросить NotFoundException если комментарий чужой")
        void updateCommentByAuthor_foreignComment_throwsNotFound() {
            User anotherUser = User.builder().id(3L).name("Another").email("another@test.ru").build();
            comment.setAuthor(anotherUser);

            when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

            assertThatThrownBy(() -> commentService.updateCommentByAuthor(1L, 100L,
                    UpdateCommentDto.builder().text("Text").build()))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("не принадлежит");
        }

        @Test
        @DisplayName("Должен выбросить ConflictException если комментарий удалён")
        void updateCommentByAuthor_deletedComment_throwsConflict() {
            comment.setStatus(CommentStatus.DELETED);
            when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

            assertThatThrownBy(() -> commentService.updateCommentByAuthor(1L, 100L,
                    UpdateCommentDto.builder().text("Text").build()))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("Удалённый комментарий");
        }
    }

    @Nested
    @DisplayName("deleteCommentByAuthor")
    class DeleteCommentByAuthorTests {

        @Test
        @DisplayName("Должен пометить комментарий как удалённый")
        void deleteCommentByAuthor_success() {
            when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));
            when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

            commentService.deleteCommentByAuthor(1L, 100L);

            ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
            verify(commentRepository).save(captor.capture());
            Comment saved = captor.getValue();

            assertThat(saved.getStatus()).isEqualTo(CommentStatus.DELETED);
            assertThat(saved.getText()).isEqualTo("[удалено автором]");
            assertThat(saved.getUpdatedOn()).isNotNull();
        }

        @Test
        @DisplayName("Не должен сохранять повторно уже удалённый комментарий")
        void deleteCommentByAuthor_alreadyDeleted_noSave() {
            comment.setStatus(CommentStatus.DELETED);
            when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

            commentService.deleteCommentByAuthor(1L, 100L);

            verify(commentRepository, never()).save(any(Comment.class));
        }
    }

    @Nested
    @DisplayName("Public and admin reads")
    class ReadCommentsTests {

        @Test
        @DisplayName("Должен вернуть опубликованные комментарии события")
        void getPublishedComments_success() {
            when(eventRepository.findByIdAndState(10L, EventState.PUBLISHED)).thenReturn(Optional.of(publishedEvent));
            when(commentRepository.findAllByEventIdAndStatus(anyLong(), any(CommentStatus.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(comment)));
            when(commentMapper.toDtoList(any())).thenReturn(List.of(commentDto));

            List<CommentDto> result = commentService.getPublishedComments(10L, 0, 10);

            assertThat(result).hasSize(1);
            verify(commentRepository).findAllByEventIdAndStatus(anyLong(), any(CommentStatus.class), any(Pageable.class));
        }

        @Test
        @DisplayName("Должен выбросить ValidationException при некорректной пагинации")
        void getPublishedComments_invalidPagination_throwsValidation() {
            assertThatThrownBy(() -> commentService.getPublishedComments(10L, 0, 0))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("size must be");
        }

        @Test
        @DisplayName("Должен выбросить NotFoundException если опубликованный комментарий не найден")
        void getPublishedCommentById_notFound_throwsNotFound() {
            when(eventRepository.findByIdAndState(10L, EventState.PUBLISHED)).thenReturn(Optional.of(publishedEvent));
            when(commentRepository.findByIdAndEventIdAndStatus(100L, 10L, CommentStatus.PUBLISHED))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> commentService.getPublishedCommentById(10L, 100L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Комментарий не найден");
        }
    }

    @Nested
    @DisplayName("moderateComment and admin delete")
    class ModerateAndDeleteTests {

        @Test
        @DisplayName("Должен опубликовать комментарий")
        void moderateComment_publish_success() {
            AdminUpdateCommentRequest request = AdminUpdateCommentRequest.builder()
                    .action(AdminUpdateCommentRequest.Action.PUBLISH)
                    .build();

            when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));
            when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);

            commentService.moderateComment(100L, request);

            ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
            verify(commentRepository).save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(CommentStatus.PUBLISHED);
        }

        @Test
        @DisplayName("Должен установить дефолтную причину при отклонении без заметки")
        void moderateComment_rejectWithoutNote_setsDefaultReason() {
            AdminUpdateCommentRequest request = AdminUpdateCommentRequest.builder()
                    .action(AdminUpdateCommentRequest.Action.REJECT)
                    .moderationNote("  ")
                    .build();

            when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));
            when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);

            commentService.moderateComment(100L, request);

            ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
            verify(commentRepository).save(captor.capture());
            Comment saved = captor.getValue();

            assertThat(saved.getStatus()).isEqualTo(CommentStatus.REJECTED);
            assertThat(saved.getModerationNote()).isEqualTo("Отклонено администратором");
        }

        @Test
        @DisplayName("Должен выбросить ConflictException при модерации удалённого комментария")
        void moderateComment_deletedComment_throwsConflict() {
            comment.setStatus(CommentStatus.DELETED);
            AdminUpdateCommentRequest request = AdminUpdateCommentRequest.builder()
                    .action(AdminUpdateCommentRequest.Action.PUBLISH)
                    .build();

            when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

            assertThatThrownBy(() -> commentService.moderateComment(100L, request))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("Удалённый комментарий");
        }

        @Test
        @DisplayName("Должен удалить комментарий администратором")
        void deleteCommentByAdmin_success() {
            when(commentRepository.existsById(100L)).thenReturn(true);

            commentService.deleteCommentByAdmin(100L);

            verify(commentRepository).deleteById(100L);
        }

        @Test
        @DisplayName("Должен выбросить NotFoundException если комментарий не существует")
        void deleteCommentByAdmin_notFound_throwsNotFound() {
            when(commentRepository.existsById(100L)).thenReturn(false);

            assertThatThrownBy(() -> commentService.deleteCommentByAdmin(100L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Комментарий не найден");
        }
    }
}
