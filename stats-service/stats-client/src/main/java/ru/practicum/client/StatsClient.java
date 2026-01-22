package ru.practicum.client;

import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointHitDto;

public class StatsClient {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public StatsClient(RestTemplate restTemplate, String serverUrl) {
        this.restTemplate = restTemplate;
        this.serverUrl = serverUrl;
    }

    public void hit(EndpointHitDto dto) {
        restTemplate.postForEntity(
                serverUrl + "/hit",
                dto,
                Void.class
        );
    }
}
