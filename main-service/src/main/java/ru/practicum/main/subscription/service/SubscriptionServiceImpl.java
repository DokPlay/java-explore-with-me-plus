package ru.practicum.main.subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.subscription.dto.SubscriptionDto;
import ru.practicum.main.subscription.mapper.SubscriptionMapper;
import ru.practicum.main.subscription.model.Subscription;
import ru.practicum.main.subscription.repository.SubscriptionRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;
import ru.practicum.main.util.PaginationValidator;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Subscription service implementation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Override
    @Transactional
    public SubscriptionDto subscribe(Long followerId, Long followingId) {
        log.info("Подписка пользователя followerId={} на пользователя followingId={}", followerId, followingId);
        if (followerId.equals(followingId)) {
            throw new ConflictException("Пользователь не может подписаться на самого себя");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + followerId));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + followingId));

        if (subscriptionRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new ConflictException("Подписка уже существует");
        }

        Subscription subscription = Subscription.builder()
                .follower(follower)
                .following(following)
                .createdOn(nowTruncatedToMillis())
                .build();

        Subscription saved = subscriptionRepository.save(subscription);
        return subscriptionMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void unsubscribe(Long followerId, Long followingId) {
        log.info("Отписка пользователя followerId={} от пользователя followingId={}", followerId, followingId);
        validateUserExists(followerId);
        validateUserExists(followingId);

        Subscription subscription = subscriptionRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new NotFoundException("Подписка не найдена"));

        subscriptionRepository.delete(subscription);
    }

    @Override
    public List<SubscriptionDto> getFollowing(Long followerId, int from, int size) {
        log.info("Получение подписок пользователя followerId={}, from={}, size={}", followerId, from, size);
        PaginationValidator.validatePagination(from, size);
        validateUserExists(followerId);

        Pageable pageable = PageRequest.of(
                from / size,
                size,
                Sort.by("createdOn").descending().and(Sort.by("id").descending())
        );

        List<Subscription> subscriptions = subscriptionRepository.findAllByFollowerId(followerId, pageable)
                .getContent();
        return subscriptionMapper.toDtoList(subscriptions);
    }

    @Override
    public List<SubscriptionDto> getFollowers(Long userId, int from, int size) {
        log.info("Получение подписчиков пользователя userId={}, from={}, size={}", userId, from, size);
        PaginationValidator.validatePagination(from, size);
        validateUserExists(userId);

        Pageable pageable = PageRequest.of(
                from / size,
                size,
                Sort.by("createdOn").descending().and(Sort.by("id").descending())
        );

        List<Subscription> subscriptions = subscriptionRepository.findAllByFollowingId(userId, pageable)
                .getContent();
        return subscriptionMapper.toDtoList(subscriptions);
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден: id=" + userId);
        }
    }

    private LocalDateTime nowTruncatedToMillis() {
        return LocalDateTime.now()
                .truncatedTo(ChronoUnit.MILLIS);
    }
}
