package ru.practicum.main.request.dto;

import lombok.Data;
import ru.practicum.main.request.status.RequestStatus;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;  // Список ID заявок
    private RequestStatus status;   // Новый статус: CONFIRMED или REJECTED
}
