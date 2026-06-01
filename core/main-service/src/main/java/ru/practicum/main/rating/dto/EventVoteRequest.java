package ru.practicum.main.rating.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.rating.status.VoteType;

/**
 * Request DTO for setting event vote.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventVoteRequest {

    /**
     * Vote type to set.
     */
    @NotNull(message = "Тип голоса обязателен")
    private VoteType vote;
}
