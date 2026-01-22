package ru.practicum.server.service;

import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.repository.EndpointHitRepository;

@Service
public class StatsServiceImpl implements StatsService {

    private final EndpointHitRepository repository;

    public StatsServiceImpl(EndpointHitRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveHit(EndpointHitDto dto) {
        EndpointHit hit = new EndpointHit(
                dto.getApp(),
                dto.getUri(),
                dto.getIp(),
                dto.getTimestamp()
        );
        repository.save(hit);
    }
}
