package ru.practicum.main.moderation.model;

import jakarta.persistence.Column;
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
import ru.practicum.main.event.model.Event;
import ru.practicum.main.moderation.status.EventModerationAction;

import java.time.LocalDateTime;

/**
 * Event moderation history entry.
 */
@Entity
@Table(name = "event_moderation_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventModerationLog {

    /**
     * Log identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Event reference.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    /**
     * Moderator action.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventModerationAction action;

    /**
     * Optional action note.
     */
    @Column(length = 1000)
    private String note;

    /**
     * Timestamp when the action was performed.
     */
    @Column(name = "acted_on", nullable = false)
    private LocalDateTime actedOn;
}
