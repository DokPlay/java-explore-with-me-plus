package ru.practicum.main.subscription.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Subscription API DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDto {

    /**
     * Subscription id.
     */
    private Long id;

    /**
     * Follower user id.
     */
    private Long followerId;

    /**
     * Followed user id.
     */
    private Long followingId;

    /**
     * Subscription creation time.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
}
