package ru.practicum.main.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import ru.practicum.client.StatsClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для StatsClientConfig.
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("StatsClientConfig Tests")
class StatsClientConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @MockBean
    private StatsClient statsClient;

    @Test
    @DisplayName("RestTemplate bean должен быть создан")
    void restTemplateBean_IsCreated() {
        // When
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

        // Then
        assertThat(restTemplate).isNotNull();
    }

    @Test
    @DisplayName("RestTemplate должен быть singleton")
    void restTemplateBean_IsSingleton() {
        // When
        RestTemplate restTemplate1 = applicationContext.getBean(RestTemplate.class);
        RestTemplate restTemplate2 = applicationContext.getBean(RestTemplate.class);

        // Then
        assertThat(restTemplate1).isSameAs(restTemplate2);
    }
}
