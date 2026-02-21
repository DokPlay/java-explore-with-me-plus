package ru.practicum.main.rating.service;

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
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.rating.dto.EventRatingSummaryDto;
import ru.practicum.main.rating.dto.EventVoteDto;
import ru.practicum.main.rating.dto.EventVoteRequest;
import ru.practicum.main.rating.mapper.EventRatingMapper;
import ru.practicum.main.rating.model.EventRating;
import ru.practicum.main.rating.repository.EventRatingRepository;
import ru.practicum.main.rating.status.VoteType;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

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
 * Unit tests for {@link EventRatingServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EventRatingService Unit Tests")
class EventRatingServiceImplTest {

    @Mock
    private EventRatingRepository eventRatingRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRatingMapper eventRatingMapper;

    @InjectMocks
    private EventRatingServiceImpl eventRatingService;

    @Nested
    @DisplayName("upsertVote")
    class UpsertVoteTests {

        @Test
        @DisplayName("Должен создать новый лайк")
        void upsertVote_createLike_success() {
            User voter = User.builder().id(1L).name("Voter").email("voter@test.com").build();
            User initiator = User.builder().id(2L).name("Initiator").email("init@test.com").build();
            Event event = Event.builder().id(10L).state(EventState.PUBLISHED).initiator(initiator).build();

            EventVoteRequest request = EventVoteRequest.builder().vote(VoteType.LIKE).build();
            EventRating saved = EventRating.builder().id(100L).event(event).user(voter).vote(VoteType.LIKE).build();
            EventVoteDto dto = EventVoteDto.builder().id(100L).eventId(10L).userId(1L).vote(VoteType.LIKE).build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(voter));
            when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
            when(eventRatingRepository.findByUserIdAndEventId(1L, 10L)).thenReturn(Optional.empty());
            when(eventRatingRepository.save(any(EventRating.class))).thenReturn(saved);
            when(eventRatingMapper.toDto(saved)).thenReturn(dto);

            EventVoteDto result = eventRatingService.upsertVote(1L, 10L, request);

            assertThat(result).isNotNull();
            assertThat(result.getVote()).isEqualTo(VoteType.LIKE);
            verify(eventRatingRepository).save(any(EventRating.class));
        }

        @Test
        @DisplayName("Должен обновить существующий голос")
        void upsertVote_updateExisting_success() {
            User voter = User.builder().id(1L).name("Voter").email("voter@test.com").build();
            User initiator = User.builder().id(2L).name("Initiator").email("init@test.com").build();
            Event event = Event.builder().id(10L).state(EventState.PUBLISHED).initiator(initiator).build();
            EventRating existing = EventRating.builder().id(100L).event(event).user(voter).vote(VoteType.LIKE).build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(voter));
            when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
            when(eventRatingRepository.findByUserIdAndEventId(1L, 10L)).thenReturn(Optional.of(existing));
            when(eventRatingRepository.save(any(EventRating.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(eventRatingMapper.toDto(any(EventRating.class)))
                    .thenReturn(EventVoteDto.builder().id(100L).eventId(10L).userId(1L).vote(VoteType.DISLIKE).build());

            eventRatingService.upsertVote(1L, 10L, EventVoteRequest.builder().vote(VoteType.DISLIKE).build());

            ArgumentCaptor<EventRating> captor = ArgumentCaptor.forClass(EventRating.class);
            verify(eventRatingRepository).save(captor.capture());
            assertThat(captor.getValue().getVote()).isEqualTo(VoteType.DISLIKE);
        }

        @Test
        @DisplayName("Должен выбросить ConflictException для неопубликованного события")
        void upsertVote_eventNotPublished_throwsConflict() {
            User voter = User.builder().id(1L).name("Voter").email("voter@test.com").build();
            User initiator = User.builder().id(2L).name("Initiator").email("init@test.com").build();
            Event event = Event.builder().id(10L).state(EventState.PENDING).initiator(initiator).build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(voter));
            when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

            assertThatThrownBy(() -> eventRatingService.upsertVote(1L, 10L,
                    EventVoteRequest.builder().vote(VoteType.LIKE).build()))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("опубликованные");
        }

        @Test
        @DisplayName("Должен выбросить ConflictException при оценке своего события")
        void upsertVote_ownEvent_throwsConflict() {
            User user = User.builder().id(1L).name("User").email("user@test.com").build();
            Event event = Event.builder().id(10L).state(EventState.PUBLISHED).initiator(user).build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

            assertThatThrownBy(() -> eventRatingService.upsertVote(1L, 10L,
                    EventVoteRequest.builder().vote(VoteType.LIKE).build()))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("своё событие");
            verify(eventRatingRepository, never()).save(any(EventRating.class));
        }
    }

    @Nested
    @DisplayName("deleteVote and read rating")
    class DeleteAndReadTests {

        @Test
        @DisplayName("Должен удалить голос")
        void deleteVote_success() {
            EventRating rating = EventRating.builder().id(100L).build();
            when(userRepository.existsById(1L)).thenReturn(true);
            when(eventRepository.existsById(10L)).thenReturn(true);
            when(eventRatingRepository.findByUserIdAndEventId(1L, 10L)).thenReturn(Optional.of(rating));

            eventRatingService.deleteVote(1L, 10L);

            verify(eventRatingRepository).delete(rating);
        }

        @Test
        @DisplayName("Должен выбросить NotFoundException если голос не найден")
        void deleteVote_notFound_throwsNotFound() {
            when(userRepository.existsById(1L)).thenReturn(true);
            when(eventRepository.existsById(10L)).thenReturn(true);
            when(eventRatingRepository.findByUserIdAndEventId(1L, 10L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> eventRatingService.deleteVote(1L, 10L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("не найден");
        }

        @Test
        @DisplayName("Должен вернуть агрегированный рейтинг события")
        void getEventRating_success() {
            Event event = Event.builder().id(10L).state(EventState.PUBLISHED).build();
            when(eventRepository.findByIdAndState(10L, EventState.PUBLISHED)).thenReturn(Optional.of(event));
            when(eventRatingRepository.countByEventIdAndVote(10L, VoteType.LIKE)).thenReturn(8L);
            when(eventRatingRepository.countByEventIdAndVote(10L, VoteType.DISLIKE)).thenReturn(3L);

            EventRatingSummaryDto result = eventRatingService.getEventRating(10L);

            assertThat(result.getLikes()).isEqualTo(8L);
            assertThat(result.getDislikes()).isEqualTo(3L);
            assertThat(result.getScore()).isEqualTo(5L);
        }

        @Test
        @DisplayName("Должен вернуть голоса пользователя")
        void getUserVotes_success() {
            when(userRepository.existsById(1L)).thenReturn(true);
            when(eventRatingRepository.findAllByUserId(anyLong(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(EventRating.builder().id(1L).build())));
            when(eventRatingMapper.toDtoList(any())).thenReturn(
                    List.of(EventVoteDto.builder().id(1L).eventId(10L).userId(1L).vote(VoteType.LIKE).build()));

            List<EventVoteDto> result = eventRatingService.getUserVotes(1L, 0, 10);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getUserId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Должен выбросить ValidationException при невалидной пагинации")
        void getUserVotes_invalidPagination_throwsValidation() {
            assertThatThrownBy(() -> eventRatingService.getUserVotes(1L, 0, 0))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("size must be");
        }
    }
}
