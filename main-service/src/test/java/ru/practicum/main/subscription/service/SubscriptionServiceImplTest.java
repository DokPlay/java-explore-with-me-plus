package ru.practicum.main.subscription.service;

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
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.subscription.dto.SubscriptionDto;
import ru.practicum.main.subscription.mapper.SubscriptionMapper;
import ru.practicum.main.subscription.model.Subscription;
import ru.practicum.main.subscription.repository.SubscriptionRepository;
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
 * Unit tests for {@link SubscriptionServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SubscriptionService Unit Tests")
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @Nested
    @DisplayName("subscribe")
    class SubscribeTests {

        @Test
        @DisplayName("Должен создать подписку")
        void subscribe_success() {
            User follower = User.builder().id(1L).name("Follower").email("follower@test.com").build();
            User following = User.builder().id(2L).name("Following").email("following@test.com").build();

            Subscription saved = Subscription.builder()
                    .id(100L)
                    .follower(follower)
                    .following(following)
                    .build();
            SubscriptionDto dto = SubscriptionDto.builder().id(100L).followerId(1L).followingId(2L).build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
            when(userRepository.findById(2L)).thenReturn(Optional.of(following));
            when(subscriptionRepository.existsByFollowerIdAndFollowingId(1L, 2L)).thenReturn(false);
            when(subscriptionRepository.save(any(Subscription.class))).thenReturn(saved);
            when(subscriptionMapper.toDto(saved)).thenReturn(dto);

            SubscriptionDto result = subscriptionService.subscribe(1L, 2L);

            assertThat(result).isNotNull();
            assertThat(result.getFollowerId()).isEqualTo(1L);
            assertThat(result.getFollowingId()).isEqualTo(2L);
            verify(subscriptionRepository).save(any(Subscription.class));
        }

        @Test
        @DisplayName("Должен выбросить ConflictException при подписке на себя")
        void subscribe_selfSubscription_throwsConflict() {
            assertThatThrownBy(() -> subscriptionService.subscribe(1L, 1L))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("самого себя");

            verify(subscriptionRepository, never()).save(any(Subscription.class));
        }

        @Test
        @DisplayName("Должен выбросить ConflictException при дубликате подписки")
        void subscribe_duplicate_throwsConflict() {
            User follower = User.builder().id(1L).name("Follower").email("follower@test.com").build();
            User following = User.builder().id(2L).name("Following").email("following@test.com").build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
            when(userRepository.findById(2L)).thenReturn(Optional.of(following));
            when(subscriptionRepository.existsByFollowerIdAndFollowingId(1L, 2L)).thenReturn(true);

            assertThatThrownBy(() -> subscriptionService.subscribe(1L, 2L))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("уже существует");
        }
    }

    @Nested
    @DisplayName("unsubscribe")
    class UnsubscribeTests {

        @Test
        @DisplayName("Должен удалить существующую подписку")
        void unsubscribe_success() {
            User follower = User.builder().id(1L).name("Follower").email("follower@test.com").build();
            User following = User.builder().id(2L).name("Following").email("following@test.com").build();
            Subscription subscription = Subscription.builder()
                    .id(100L)
                    .follower(follower)
                    .following(following)
                    .build();

            when(userRepository.existsById(1L)).thenReturn(true);
            when(userRepository.existsById(2L)).thenReturn(true);
            when(subscriptionRepository.findByFollowerIdAndFollowingId(1L, 2L))
                    .thenReturn(Optional.of(subscription));

            subscriptionService.unsubscribe(1L, 2L);

            ArgumentCaptor<Subscription> captor = ArgumentCaptor.forClass(Subscription.class);
            verify(subscriptionRepository).delete(captor.capture());
            assertThat(captor.getValue().getId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("Должен выбросить NotFoundException при отсутствии подписки")
        void unsubscribe_notFound_throwsNotFound() {
            when(userRepository.existsById(1L)).thenReturn(true);
            when(userRepository.existsById(2L)).thenReturn(true);
            when(subscriptionRepository.findByFollowerIdAndFollowingId(1L, 2L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> subscriptionService.unsubscribe(1L, 2L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Подписка не найдена");
        }
    }

    @Nested
    @DisplayName("get following/followers")
    class ReadSubscriptionsTests {

        @Test
        @DisplayName("Должен вернуть список подписок пользователя")
        void getFollowing_success() {
            SubscriptionDto dto = SubscriptionDto.builder().id(1L).followerId(1L).followingId(2L).build();

            when(userRepository.existsById(1L)).thenReturn(true);
            when(subscriptionRepository.findAllByFollowerId(anyLong(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(Subscription.builder().id(1L).build())));
            when(subscriptionMapper.toDtoList(any())).thenReturn(List.of(dto));

            List<SubscriptionDto> result = subscriptionService.getFollowing(1L, 0, 10);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getFollowerId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Должен вернуть список подписчиков пользователя")
        void getFollowers_success() {
            SubscriptionDto dto = SubscriptionDto.builder().id(1L).followerId(2L).followingId(1L).build();

            when(userRepository.existsById(1L)).thenReturn(true);
            when(subscriptionRepository.findAllByFollowingId(anyLong(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(Subscription.builder().id(1L).build())));
            when(subscriptionMapper.toDtoList(any())).thenReturn(List.of(dto));

            List<SubscriptionDto> result = subscriptionService.getFollowers(1L, 0, 10);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getFollowingId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Должен выбросить ValidationException при невалидной пагинации")
        void getFollowing_invalidPagination_throwsValidation() {
            assertThatThrownBy(() -> subscriptionService.getFollowing(1L, 0, 0))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("size must be");
        }
    }
}
