package ru.practicum.main.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.request.status.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    private Long id;             // ID заявки
    private Long event;          // ID события
    private Long requester;      // ID пользователя
    private LocalDateTime created; // Дата создания
    private RequestStatus status;  // Статус
}
