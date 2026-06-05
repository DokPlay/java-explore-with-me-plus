package ru.practicum.requestservice.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.request.status.RequestStatus;

@RestController
@RequestMapping("/internal/requests")
@RequiredArgsConstructor
public class InternalRequestController {

    private final RequestRepository requestRepository;

    @GetMapping("/events/{eventId}/count")
    public long getConfirmedRequestsCount(@PathVariable long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }
}
