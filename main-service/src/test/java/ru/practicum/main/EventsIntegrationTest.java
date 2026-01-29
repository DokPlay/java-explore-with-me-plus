package ru.practicum.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.dto.LocationDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для API событий.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Events Integration Tests")
class EventsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EventRepository eventRepository;

    @MockBean
    private StatsClient statsClient;

    private User testUser;
    private Category testCategory;
    private Event testEvent;

    @BeforeEach
    void setUp() {
        // Создаём тестового пользователя
        testUser = new User();
        testUser.setName("Integration Test User");
        testUser.setEmail("integration@test.com");
        testUser = userRepository.save(testUser);

        // Создаём тестовую категорию
        testCategory = new Category();
        testCategory.setName("Integration Test Category");
        testCategory = categoryRepository.save(testCategory);

        // Создаём локацию
        Location location = new Location();
        location.setLat(55.75f);
        location.setLon(37.62f);

        // Создаём тестовое событие
        testEvent = Event.builder()
                .title("Integration Test Event")
                .annotation("This is an integration test annotation that is long enough")
                .description("This is a detailed description for integration testing")
                .eventDate(LocalDateTime.now().plusDays(7))
                .createdOn(LocalDateTime.now())
                .initiator(testUser)
                .category(testCategory)
                .location(location)
                .paid(false)
                .participantLimit(100)
                .requestModeration(true)
                .state(EventState.PUBLISHED)
                .publishedOn(LocalDateTime.now())
                .confirmedRequests(0)
                .build();
        testEvent = eventRepository.save(testEvent);

        // Мокаем StatsClient
        when(statsClient.getStats(any())).thenReturn(Collections.emptyList());
    }

    @Nested
    @DisplayName("Public API")
    class PublicApiTests {

        @Test
        @DisplayName("GET /events - должен вернуть список событий")
        void getPublicEvents_ReturnsEvents() throws Exception {
            mockMvc.perform(get("/events")
                            .param("from", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("GET /events/{id} - должен вернуть опубликованное событие")
        void getPublicEventById_ReturnsEvent() throws Exception {
            mockMvc.perform(get("/events/{id}", testEvent.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testEvent.getId()))
                    .andExpect(jsonPath("$.title").value("Integration Test Event"));
        }

        @Test
        @DisplayName("GET /events/{id} - должен вернуть 404 для несуществующего события")
        void getPublicEventById_NotFound_Returns404() throws Exception {
            mockMvc.perform(get("/events/{id}", 99999L))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /events - фильтрация по тексту")
        void getPublicEvents_WithTextFilter_ReturnsFilteredEvents() throws Exception {
            mockMvc.perform(get("/events")
                            .param("text", "Integration")
                            .param("from", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("GET /events - фильтрация по категориям")
        void getPublicEvents_WithCategoryFilter_ReturnsFilteredEvents() throws Exception {
            mockMvc.perform(get("/events")
                            .param("categories", testCategory.getId().toString())
                            .param("from", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("GET /events - фильтрация по платности")
        void getPublicEvents_WithPaidFilter_ReturnsFilteredEvents() throws Exception {
            mockMvc.perform(get("/events")
                            .param("paid", "false")
                            .param("from", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }

    @Nested
    @DisplayName("Private API")
    class PrivateApiTests {

        @Test
        @DisplayName("GET /users/{userId}/events - должен вернуть события пользователя")
        void getUserEvents_ReturnsUserEvents() throws Exception {
            mockMvc.perform(get("/users/{userId}/events", testUser.getId())
                            .param("from", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("POST /users/{userId}/events - должен создать новое событие")
        void createEvent_ValidData_ReturnsCreatedEvent() throws Exception {
            NewEventDto newEventDto = NewEventDto.builder()
                    .title("New Integration Test Event")
                    .annotation("This annotation is long enough for the validation to pass")
                    .description("This is a detailed description that meets the minimum length requirement")
                    .eventDate(LocalDateTime.now().plusDays(14))
                    .category(testCategory.getId())
                    .location(new LocationDto(55.75f, 37.62f))
                    .paid(true)
                    .participantLimit(50)
                    .requestModeration(false)
                    .build();

            mockMvc.perform(post("/users/{userId}/events", testUser.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newEventDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value("New Integration Test Event"))
                    .andExpect(jsonPath("$.paid").value(true));
        }

        @Test
        @DisplayName("GET /users/{userId}/events/{eventId} - должен вернуть событие пользователя")
        void getUserEventById_ReturnsEvent() throws Exception {
            mockMvc.perform(get("/users/{userId}/events/{eventId}", 
                            testUser.getId(), testEvent.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testEvent.getId()));
        }

        @Test
        @DisplayName("POST /users/{userId}/events - невалидные данные возвращают 400")
        void createEvent_InvalidData_Returns400() throws Exception {
            NewEventDto invalidDto = NewEventDto.builder()
                    .title("") // пустой title
                    .build();

            mockMvc.perform(post("/users/{userId}/events", testUser.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("GET /users/{userId}/events - несуществующий пользователь возвращает 404")
        void getUserEvents_NonExistentUser_Returns404() throws Exception {
            mockMvc.perform(get("/users/{userId}/events", 99999L)
                            .param("from", "0")
                            .param("size", "10"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Admin API")
    class AdminApiTests {

        @Test
        @DisplayName("GET /admin/events - должен вернуть события с фильтрами")
        void getAdminEvents_ReturnsEvents() throws Exception {
            mockMvc.perform(get("/admin/events")
                            .param("from", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("GET /admin/events - фильтрация по пользователям")
        void getAdminEvents_WithUserFilter_ReturnsFilteredEvents() throws Exception {
            mockMvc.perform(get("/admin/events")
                            .param("users", testUser.getId().toString())
                            .param("from", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("GET /admin/events - фильтрация по состояниям")
        void getAdminEvents_WithStateFilter_ReturnsFilteredEvents() throws Exception {
            mockMvc.perform(get("/admin/events")
                            .param("states", "PUBLISHED")
                            .param("from", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("GET /admin/events - фильтрация по категориям")
        void getAdminEvents_WithCategoryFilter_ReturnsFilteredEvents() throws Exception {
            mockMvc.perform(get("/admin/events")
                            .param("categories", testCategory.getId().toString())
                            .param("from", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("GET /admin/events - фильтрация по датам")
        void getAdminEvents_WithDateFilter_ReturnsFilteredEvents() throws Exception {
            String rangeStart = LocalDateTime.now().minusDays(1).toString();
            String rangeEnd = LocalDateTime.now().plusDays(30).toString();

            mockMvc.perform(get("/admin/events")
                            .param("rangeStart", rangeStart)
                            .param("rangeEnd", rangeEnd)
                            .param("from", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("PATCH /admin/events/{eventId} - несуществующее событие возвращает 404")
        void updateAdminEvent_NonExistentEvent_Returns404() throws Exception {
            String updateJson = "{\"title\": \"Updated Title\"}";

            mockMvc.perform(patch("/admin/events/{eventId}", 99999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateJson))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("POST - слишком короткий title возвращает 400")
        void createEvent_ShortTitle_Returns400() throws Exception {
            NewEventDto dto = NewEventDto.builder()
                    .title("ab") // меньше 3 символов
                    .annotation("Valid annotation that is long enough for the test")
                    .description("Valid description that is long enough")
                    .eventDate(LocalDateTime.now().plusDays(7))
                    .category(testCategory.getId())
                    .location(new LocationDto(55.75f, 37.62f))
                    .build();

            mockMvc.perform(post("/users/{userId}/events", testUser.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST - дата события в прошлом возвращает 400")
        void createEvent_PastDate_Returns400() throws Exception {
            NewEventDto dto = NewEventDto.builder()
                    .title("Valid Title Event")
                    .annotation("Valid annotation that is long enough for the test")
                    .description("Valid description that is long enough")
                    .eventDate(LocalDateTime.now().minusDays(1)) // дата в прошлом
                    .category(testCategory.getId())
                    .location(new LocationDto(55.75f, 37.62f))
                    .build();

            mockMvc.perform(post("/users/{userId}/events", testUser.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST - несуществующая категория возвращает 404")
        void createEvent_NonExistentCategory_Returns404() throws Exception {
            NewEventDto dto = NewEventDto.builder()
                    .title("Valid Title Event")
                    .annotation("Valid annotation that is long enough for the test")
                    .description("Valid description that is long enough")
                    .eventDate(LocalDateTime.now().plusDays(7))
                    .category(99999L) // несуществующая категория
                    .location(new LocationDto(55.75f, 37.62f))
                    .build();

            mockMvc.perform(post("/users/{userId}/events", testUser.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Pagination Tests")
    class PaginationTests {

        @Test
        @DisplayName("GET /events - пагинация работает корректно")
        void getPublicEvents_Pagination_Works() throws Exception {
            mockMvc.perform(get("/events")
                            .param("from", "0")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.lessThanOrEqualTo(5)));
        }

        @Test
        @DisplayName("GET /events - вторая страница")
        void getPublicEvents_SecondPage_Works() throws Exception {
            mockMvc.perform(get("/events")
                            .param("from", "5")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }
}
