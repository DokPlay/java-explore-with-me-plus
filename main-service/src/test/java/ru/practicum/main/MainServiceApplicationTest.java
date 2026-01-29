package ru.practicum.main;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.client.StatsClient;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Интеграционный тест для проверки загрузки контекста приложения.
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("MainServiceApplication Integration Test")
class MainServiceApplicationTest {

    @MockBean
    private StatsClient statsClient;

    @Test
    @DisplayName("Контекст приложения должен загружаться без ошибок")
    void contextLoads() {
        assertDoesNotThrow(() -> {});
    }
}
