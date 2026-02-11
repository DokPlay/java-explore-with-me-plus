package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.DateTimeFormatConstants;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;

import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * REST client for interacting with Stats Service.
 * <p>
 * Provides operations to record views and retrieve statistics.
 * The Stats Server URL is configured via {@code stats-server.url}.
 */
@Component
public class StatsClient {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(DateTimeFormatConstants.DATE_TIME_PATTERN);

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
    public List<StatsResponseDto> getStats(StatsRequestDto requestDto) {
        String start = requestDto.getStart().format(DATE_TIME_FORMATTER);
        String end = requestDto.getEnd().format(DATE_TIME_FORMATTER);
        Boolean unique = requestDto.getUnique() != null ? requestDto.getUnique() : false;

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique);

        if (requestDto.getUris() != null && !requestDto.getUris().isEmpty()) {
            for (String uri : requestDto.getUris()) {
                builder.queryParam("uris", uri);
            }
        }

        URI uri = builder.encode().build().toUri();

        ResponseEntity<List<StatsResponseDto>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StatsResponseDto>>() {});

        return response.getBody();
    }
}
