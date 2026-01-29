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
 * REST-клиент для взаимодействия со Stats Service.
 * <p>
 * Предоставляет методы для:
 * <ul>
 *     <li>Сохранения информации о просмотре эндпоинта ({@link #hit})</li>
 *     <li>Получения статистики просмотров ({@link #getStats})</li>
 * </ul>
 *
 * <h2>Конфигурация:</h2>
 * <p>URL Stats Server задаётся через property {@code stats-server.url} в application.properties.</p>
 *
 * <h2>Использование в Main Service:</h2>
 * <pre>{@code
 * @Autowired
 * private StatsClient statsClient;
 *
 * // Сохранить просмотр
 * statsClient.hit(EndpointHitDto.builder()
 *     .app("ewm-main-service")
 *     .uri("/events/1")
 *     .ip("192.168.1.1")
 *     .timestamp(LocalDateTime.now())
 *     .build());
 *
 * // Получить статистику
 * List<StatsResponseDto> stats = statsClient.getStats(requestDto);
 * }</pre>
 *
 * @author ExploreWithMe Team
 * @version 1.0
 * @see ru.practicum.dto.EndpointHitDto
 * @see ru.practicum.dto.StatsRequestDto
 */
@Component
public class StatsClient {

    private final String serverUrl;
    private final RestTemplate restTemplate;

    /**
     * Конструктор с внедрением зависимостей Spring.
     *
     * @param serverUrl    URL Stats Server из конфигурации (stats-server.url)
     * @param restTemplate Spring-managed RestTemplate бин
     */
    public StatsClient(
            @Value("${stats-server.url}") String serverUrl,
            RestTemplate restTemplate
    ) {
        this.serverUrl = serverUrl;
        this.restTemplate = restTemplate;
    }

    /**
     * Сохраняет информацию о просмотре эндпоинта.
     * <p>
     * Отправляет POST запрос на {@code /hit} endpoint Stats Server.
     * Не возвращает результат (void), ошибки логируются на стороне вызывающего кода.
     *
     * @param endpointHitDto данные о просмотре (app, uri, ip, timestamp)
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
     * Retrieves statistics from the stats server.
     * <p>
     * Review fixes applied:
     * - Created StatsRequestDto with @NotNull validation for start/end parameters
     * - Changed return type from List<Object> to List<StatsResponseDto>
     * </p>
     *
     * @param requestDto validated request containing start, end, uris, and unique flag
     * @return list of statistics response DTOs
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
