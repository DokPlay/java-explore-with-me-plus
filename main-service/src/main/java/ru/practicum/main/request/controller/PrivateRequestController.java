package ru.practicum.main.request.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateRequestController {

    private final RequestService requestService;

    /**
     * GET /users/{userId}/requests
     * Получить свои заявки на участие в чужих событиях
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getUserRequests(
            @PathVariable  Long userId) {
        log.info("GET /users/{}/requests - Получение заявок пользователя", userId);
        return requestService.getUserRequests(userId);
    }

    /**
     * POST /users/{userId}/requests?eventId={eventId}
     * Подать заявку на участие в событии
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(
            @PathVariable  Long userId,
            @RequestParam  Long eventId) {
        log.info("POST /users/{}/requests?eventId={} - Создание заявки", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    /**
     * PATCH /users/{userId}/requests/{requestId}/cancel
     * Отменить свою заявку
     */
    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(
            @PathVariable  Long userId,
            @PathVariable  Long requestId) {
        log.info("PATCH /users/{}/requests/{}/cancel - Отмена заявки", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }
}
