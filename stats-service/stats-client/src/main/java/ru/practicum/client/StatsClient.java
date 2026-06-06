package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.client.exception.StatsServerUnavailableException;
import ru.practicum.dto.DateTimeFormatConstants;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.ViewStatsDto;

import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class StatsClient {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(DateTimeFormatConstants.DATE_TIME_PATTERN);

    private final String statsServiceId;
    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;
    private final RetryTemplate retryTemplate;

    public StatsClient(
            @Value("${stats-service.service-id:stats-service}") String statsServiceId,
            RestTemplate restTemplate,
            DiscoveryClient discoveryClient,
            RetryTemplate retryTemplate
    ) {
        this.statsServiceId = statsServiceId;
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;
        this.retryTemplate = retryTemplate;
    }

    public void hit(EndpointHitDto endpointHitDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EndpointHitDto> request = new HttpEntity<>(endpointHitDto, headers);

        restTemplate.exchange(
                makeUri("/hit"),
                HttpMethod.POST,
                request,
                Void.class
        );
    }

    public List<ViewStatsDto> getStats(StatsRequestDto requestDto) {
        String start = requestDto.getStart().format(DATE_TIME_FORMATTER);
        String end = requestDto.getEnd().format(DATE_TIME_FORMATTER);
        Boolean unique = requestDto.getUnique() != null ? requestDto.getUnique() : false;

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUri(makeUri("/stats"))
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique);

        if (requestDto.getUris() != null && !requestDto.getUris().isEmpty()) {
            for (String uri : requestDto.getUris()) {
                builder.queryParam("uris", uri);
            }
        }

        URI uri = builder.encode().build().toUri();

        ResponseEntity<List<ViewStatsDto>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }

    private URI makeUri(String path) {
        ServiceInstance instance = retryTemplate.execute(context -> getInstance());
        return URI.create("http://" + instance.getHost() + ":" + instance.getPort() + path);
    }

    private ServiceInstance getInstance() {
        return discoveryClient.getInstances(statsServiceId).stream()
                .findFirst()
                .orElseThrow(() -> new StatsServerUnavailableException(
                        "Stats service is not registered in discovery service: " + statsServiceId
                ));
    }
}
