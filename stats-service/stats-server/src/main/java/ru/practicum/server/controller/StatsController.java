package ru.practicum.server.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.server.service.StatsService;

@RestController
@RequestMapping
public class StatsController {

    private final StatsService service;

    public StatsController(StatsService service) {
        this.service = service;
    }

    @PostMapping("/hit")
    public void hit(@Valid @RequestBody EndpointHitDto dto) {
        service.saveHit(dto);
    }
}
