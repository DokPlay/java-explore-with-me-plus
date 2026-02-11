package ru.practicum.main.subscription.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.subscription.dto.SubscriptionDto;
import ru.practicum.main.subscription.service.SubscriptionService;

import java.util.List;

/**
 * Private API for user subscriptions.
 */
@RestController
@RequestMapping("/users/{userId}/subscriptions")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateSubscriptionController {

    private final SubscriptionService subscriptionService;

    /**
     * Subscribes current user to another user.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto subscribe(
            @PathVariable @Positive Long userId,
            @RequestParam @Positive Long targetUserId) {
        log.info("POST /users/{}/subscriptions?targetUserId={} - Подписка", userId, targetUserId);
        return subscriptionService.subscribe(userId, targetUserId);
    }

    /**
     * Unsubscribes current user from another user.
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(
            @PathVariable @Positive Long userId,
            @RequestParam @Positive Long targetUserId) {
        log.info("DELETE /users/{}/subscriptions?targetUserId={} - Отписка", userId, targetUserId);
        subscriptionService.unsubscribe(userId, targetUserId);
    }

    /**
     * Returns users followed by current user.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SubscriptionDto> getFollowing(
            @PathVariable @Positive Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET /users/{}/subscriptions - Получение списка подписок", userId);
        return subscriptionService.getFollowing(userId, from, size);
    }

    /**
     * Returns followers of current user.
     */
    @GetMapping("/followers")
    @ResponseStatus(HttpStatus.OK)
    public List<SubscriptionDto> getFollowers(
            @PathVariable @Positive Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET /users/{}/subscriptions/followers - Получение списка подписчиков", userId);
        return subscriptionService.getFollowers(userId, from, size);
    }
}
