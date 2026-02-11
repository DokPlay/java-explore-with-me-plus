package ru.practicum.main.event.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;

/**
 * Event entity.
 */
@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    /**
     * Unique event identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Short event description (annotation).
     */
    @Column(nullable = false, length = 2000)
    private String annotation;

    /**
     * Event category.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * Number of approved participation requests.
     */
    @Column(name = "confirmed_requests")
    @Builder.Default
    private Long confirmedRequests = 0L;

    /**
     * Event creation date and time.
     */
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    /**
     * Full event description.
     */
    @Column(length = 7000)
    private String description;

    /**
     * Event date and time.
     */
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    /**
     * Event initiator (creator).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    /**
     * Event venue coordinates.
     */
    @Embedded
    private Location location;

    /**
     * Paid flag.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean paid = false;

    /**
     * Participant limit (0 = unlimited).
     */
    @Column(name = "participant_limit")
    @Builder.Default
    private Integer participantLimit = 0;

    /**
     * Event publication date and time.
     */
    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    /**
     * Whether request pre-moderation is required.
     */
    @Column(name = "request_moderation")
    @Builder.Default
    private Boolean requestModeration = true;

    /**
     * Event state.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EventState state = EventState.PENDING;

    /**
     * Event title.
     */
    @Column(nullable = false, length = 120)
    private String title;

    /**
     * View count.
     */
    @Builder.Default
    private Long views = 0L;

    /**
     * Optional moderation note from administrator.
     */
    @Column(name = "moderation_note", length = 1000)
    private String moderationNote;
}
