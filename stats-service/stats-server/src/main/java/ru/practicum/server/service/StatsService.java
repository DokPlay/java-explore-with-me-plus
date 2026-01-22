package ru.practicum.server.service;

import ru.practicum.dto.EndpointHitDto;

public interface StatsService {
    void saveHit(EndpointHitDto dto);
}
