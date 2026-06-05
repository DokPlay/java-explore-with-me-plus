package ru.practicum.requestservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "event-service", path = "/internal/events")
public interface EventServiceClient {

    @GetMapping("/{eventId}/exists")
    boolean isEventExists(@PathVariable("eventId") long eventId);
}
