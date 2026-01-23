package ru.practicum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsRequestDto {

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    private List<String> uris;

    private Boolean unique = false;
}
