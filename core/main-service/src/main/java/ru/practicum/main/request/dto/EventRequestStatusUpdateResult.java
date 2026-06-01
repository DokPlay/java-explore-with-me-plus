package ru.practicum.main.request.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests; // Подтверждённые
    private List<ParticipationRequestDto> rejectedRequests;  // Отклонённые
}
