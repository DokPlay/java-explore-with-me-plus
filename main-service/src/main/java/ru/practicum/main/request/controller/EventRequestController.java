package ru.practicum.main.request.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventRequestController {

    private final RequestService requestService;

    /**
     * GET /users/{userId}/events/{eventId}/requests
     * Получить заявки на своё событие
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequests(
            @PathVariable  Long userId,
            @PathVariable  Long eventId) {
        log.info("GET /users/{}/events/{}/requests - Получение заявок на событие",
                userId, eventId);
        return requestService.getEventRequests(userId, eventId);
    }

    /**
     * PATCH /users/{userId}/events/{eventId}/requests
     * Подтвердить/отклонить заявки на своё событие
     */
    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable  Long userId,
            @PathVariable  Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("PATCH /users/{}/events/{}/requests - Изменение статуса заявок",
                userId, eventId);
        return requestService.updateRequestStatus(userId, eventId, updateRequest);
    }
}
