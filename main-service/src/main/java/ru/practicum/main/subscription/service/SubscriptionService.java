package ru.practicum.main.subscription.service;

import ru.practicum.main.subscription.dto.SubscriptionDto;

import java.util.List;

/**
 * Business operations for subscriptions.
 */
public interface SubscriptionService {

    /**
     * Creates a subscription from follower to followed user.
     */
    SubscriptionDto subscribe(Long followerId, Long followingId);

    /**
     * Removes an existing subscription.
     */
    void unsubscribe(Long followerId, Long followingId);

    /**
     * Returns users followed by a user.
     */
    List<SubscriptionDto> getFollowing(Long followerId, int from, int size);

    /**
     * Returns followers of a user.
     */
    List<SubscriptionDto> getFollowers(Long userId, int from, int size);
}
