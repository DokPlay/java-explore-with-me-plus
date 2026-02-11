package ru.practicum.main.location.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Admin-managed location dictionary entry.
 */
@Entity
@Table(name = "managed_locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagedLocation {

    /**
     * Location ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Human-readable location title.
     */
    @Column(nullable = false, unique = true, length = 120)
    private String name;

    /**
     * Latitude.
     */
    @Column(nullable = false)
    private Double lat;

    /**
     * Longitude.
     */
    @Column(nullable = false)
    private Double lon;

    /**
     * Activity flag.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Creation time.
     */
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    /**
     * Last update time.
     */
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;
}
