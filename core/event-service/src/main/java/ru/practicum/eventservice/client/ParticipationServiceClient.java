package ru.practicum.eventservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "participation-service", path = "/internal/requests")
public interface ParticipationServiceClient {

    @GetMapping("/events/{eventId}/count")
    long getRequestsCount(@PathVariable("eventId") long eventId);
}
