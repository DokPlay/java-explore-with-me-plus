package ru.practicum.main.subscription.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;

/**
 * Subscription relation between users.
 */
@Entity
@Table(name = "subscriptions",
        uniqueConstraints = @UniqueConstraint(name = "uq_subscription_follower_following",
                columnNames = {"follower_id", "following_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class Subscription {

    /**
     * Subscription identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    /**
     * User who follows another user.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    /**
     * User being followed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private User following;

    /**
     * Subscription creation timestamp.
     */
    @Column(name = "created_on", nullable = false)
    @ToString.Include
    private LocalDateTime createdOn;

    @ToString.Include(name = "followerId")
    private Long followerIdForToString() {
        return follower != null ? follower.getId() : null;
    }

    @ToString.Include(name = "followingId")
    private Long followingIdForToString() {
        return following != null ? following.getId() : null;
    }
}
