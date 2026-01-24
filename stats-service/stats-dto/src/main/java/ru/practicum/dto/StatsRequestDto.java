package ru.practicum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for statistics request parameters.
 * <p>
 * Review fix: Created dedicated DTO with validation to ensure
 * required parameters (start, end) are always provided.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsRequestDto {

    /** Start of the statistics period (required). */
    @NotNull
    private LocalDateTime start;

    /** End of the statistics period (required). */
    @NotNull
    private LocalDateTime end;

    private List<String> uris;

    private Boolean unique = false;
}
