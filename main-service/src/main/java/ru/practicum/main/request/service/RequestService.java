package ru.practicum.main.request.service;


import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    // ========== Методы пользователя ==========

    /**
     * Получить свои заявки на участие в чужих событиях
     */
    List<ParticipationRequestDto> getUserRequests(Long userId);

    /**
     * Подать заявку на участие в событии
     */
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    /**
     * Отменить свою заявку
     */
    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    // ========== Методы организатора события ==========

    /**
     * Получить заявки на своё событие
     */
    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    /**
     * Подтвердить/отклонить заявки на своё событие
     */
    EventRequestStatusUpdateResult updateRequestStatus(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest updateRequest);
}
