package ru.practicum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for stats request parameters.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsRequestDto {

    /** Start of the stats period (required). */
    @NotNull
    private LocalDateTime start;

    /** End of the stats period (required). */
    @NotNull
    private LocalDateTime end;

    private List<String> uris;

    private Boolean unique = false;
}
