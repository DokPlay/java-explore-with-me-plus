package ru.practicum.main.location.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Location response DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagedLocationDto {

    /**
     * Location ID.
     */
    private Long id;

    /**
     * Name.
     */
    private String name;

    /**
     * Latitude.
     */
    private Double lat;

    /**
     * Longitude.
     */
    private Double lon;

    /**
     * Active flag.
     */
    private Boolean active;

    /**
     * Creation time.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    /**
     * Update time.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedOn;
}
