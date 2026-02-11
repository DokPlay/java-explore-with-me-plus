package ru.practicum.main.subscription.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.main.subscription.model.Subscription;

import java.util.List;
import java.util.Optional;

/**
 * Repository for user subscriptions.
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    /**
     * Checks whether a subscription exists.
     */
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /**
     * Finds subscription by follower and followed user.
     */
    Optional<Subscription> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /**
     * Returns paginated users followed by the follower.
     */
    Page<Subscription> findAllByFollowerId(Long followerId, Pageable pageable);

    /**
     * Returns paginated followers of a user.
     */
    Page<Subscription> findAllByFollowingId(Long followingId, Pageable pageable);

    /**
     * Returns followed user ids for a follower.
     */
    @Query("SELECT s.following.id FROM Subscription s WHERE s.follower.id = :followerId")
    List<Long> findFollowingIdsByFollowerId(@Param("followerId") Long followerId);
}
