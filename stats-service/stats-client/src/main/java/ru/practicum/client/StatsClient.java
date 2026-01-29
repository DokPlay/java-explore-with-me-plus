package ru.practicum.client;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST client for interacting with Stats Service.
 * <p>
 * Provides operations to record views and retrieve statistics.
 * The Stats Server URL is configured via {@code stats-server.url}.
 */
@Component
public class StatsClient {

    private final String serverUrl;
    private final RestTemplate restTemplate;

        /**
         * Constructor with Spring dependency injection.
         *
         * @param serverUrl    Stats Server URL from configuration (stats-server.url)
         * @param restTemplate Spring-managed RestTemplate bean
         */
    public StatsClient(
            @Value("${stats-server.url}") String serverUrl,
            RestTemplate restTemplate
    ) {
        this.serverUrl = serverUrl;
        this.restTemplate = restTemplate;
    }

        /**
         * Records an endpoint view.
         *
         * @param endpointHitDto view data (app, uri, ip, timestamp)
         */
    @SuppressWarnings("unchecked")
    public void hit(EndpointHitDto endpointHitDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EndpointHitDto> request =
                new HttpEntity<>(endpointHitDto, headers);

        restTemplate.exchange(
                serverUrl + "/hit",
                HttpMethod.POST,
                request,
                Void.class
        );
    }

        /**
         * Returns statistics for the given parameters.
         *
         * @param requestDto period parameters, URIs, and uniqueness flag
         * @return list of aggregated statistics
         */
    @SuppressWarnings("unchecked")
    public List<StatsResponseDto> getStats(@Valid StatsRequestDto requestDto) {
        Map<String, Object> params = new HashMap<>();
        params.put("start", requestDto.getStart());
        params.put("end", requestDto.getEnd());
        params.put("unique", requestDto.getUnique());

        StringBuilder uri = new StringBuilder(
                serverUrl + "/stats?start={start}&end={end}&unique={unique}"
        );

        if (requestDto.getUris() != null && !requestDto.getUris().isEmpty()) {
            params.put("uris", requestDto.getUris());
            uri.append("&uris={uris}");
        }

        ResponseEntity<List<StatsResponseDto>> response =
                restTemplate.exchange(
                        uri.toString(),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<StatsResponseDto>>() {},
                        params
                );

        return response.getBody();
    }
}
