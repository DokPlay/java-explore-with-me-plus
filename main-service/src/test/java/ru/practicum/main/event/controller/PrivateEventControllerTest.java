package ru.practicum.main.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.service.EventService;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration тесты для PrivateEventController.
 */
@WebMvcTest(PrivateEventController.class)
@DisplayName("PrivateEventController Tests")
class PrivateEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    private ObjectMapper objectMapper;
    private EventShortDto eventShortDto;
    private EventFullDto eventFullDto;
    private NewEventDto newEventDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        eventShortDto = EventShortDto.builder()
                .id(1L)
                .title("Test Event")
                .annotation("Test annotation for event testing")
                .eventDate(LocalDateTime.now().plusDays(7))
                .paid(false)
                .build();

        eventFullDto = EventFullDto.builder()
                .id(1L)
                .title("Test Event")
                .annotation("Test annotation for event testing")
                .description("Full description of the test event")
                .eventDate(LocalDateTime.now().plusDays(7))
                .state(EventState.PENDING)
                .paid(false)
                .participantLimit(100)
                .build();

        newEventDto = NewEventDto.builder()
                .title("New Test Event")
                .annotation("Annotation must be at least 20 characters")
                .description("Description must be at least 20 characters long")
                .category(1L)
                .eventDate(LocalDateTime.now().plusDays(7))
                .location(new LocationDto(55.75f, 37.62f))
                .paid(false)
                .participantLimit(100)
                .requestModeration(true)
                .build();
    }

    @Test
    @DisplayName("GET /users/{userId}/events - должен вернуть список событий пользователя")
    void getUserEvents_ReturnsEventList() throws Exception {
        // Given
        when(eventService.getUserEvents(eq(1L), anyInt(), anyInt()))
                .thenReturn(List.of(eventShortDto));

        // When/Then
        mockMvc.perform(get("/users/1/events")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Event")));

        verify(eventService).getUserEvents(eq(1L), eq(0), eq(10));
    }

    @Test
    @DisplayName("GET /users/{userId}/events - пользователь не найден возвращает 404")
    void getUserEvents_UserNotFound_Returns404() throws Exception {
        // Given
        when(eventService.getUserEvents(eq(999L), anyInt(), anyInt()))
                .thenThrow(new NotFoundException("Пользователь не найден: id=999"));

        // When/Then
        mockMvc.perform(get("/users/999/events"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("NOT_FOUND")));
    }

    @Test
    @DisplayName("POST /users/{userId}/events - должен создать событие")
    void createEvent_ReturnsCreatedEvent() throws Exception {
        // Given
        when(eventService.createEvent(eq(1L), any(NewEventDto.class)))
                .thenReturn(eventFullDto);

        // When/Then
        mockMvc.perform(post("/users/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEventDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.state", is("PENDING")));

        verify(eventService).createEvent(eq(1L), any(NewEventDto.class));
    }

    @Test
    @DisplayName("POST /users/{userId}/events - невалидные данные возвращают 400")
    void createEvent_InvalidData_Returns400() throws Exception {
        // Given
        NewEventDto invalidDto = NewEventDto.builder()
                .title("") // пустой заголовок
                .annotation("short") // слишком короткий
                .build();

        // When/Then
        mockMvc.perform(post("/users/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /users/{userId}/events/{eventId} - должен вернуть событие")
    void getUserEventById_ReturnsEvent() throws Exception {
        // Given
        when(eventService.getUserEventById(1L, 1L)).thenReturn(eventFullDto);

        // When/Then
        mockMvc.perform(get("/users/1/events/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Event")));

        verify(eventService).getUserEventById(1L, 1L);
    }

    @Test
    @DisplayName("GET /users/{userId}/events/{eventId} - событие не найдено возвращает 404")
    void getUserEventById_NotFound_Returns404() throws Exception {
        // Given
        when(eventService.getUserEventById(1L, 999L))
                .thenThrow(new NotFoundException("Событие не найдено: id=999"));

        // When/Then
        mockMvc.perform(get("/users/1/events/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("NOT_FOUND")));
    }

    @Test
    @DisplayName("PATCH /users/{userId}/events/{eventId} - должен обновить событие")
    void updateEvent_ReturnsUpdatedEvent() throws Exception {
        // Given
        UpdateEventUserRequest updateRequest = new UpdateEventUserRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setStateAction(UpdateEventUserRequest.StateAction.SEND_TO_REVIEW);

        eventFullDto.setTitle("Updated Title");
        when(eventService.updateEventByUser(eq(1L), eq(1L), any(UpdateEventUserRequest.class)))
                .thenReturn(eventFullDto);

        // When/Then
        mockMvc.perform(patch("/users/1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Title")));

        verify(eventService).updateEventByUser(eq(1L), eq(1L), any(UpdateEventUserRequest.class));
    }

    @Test
    @DisplayName("PATCH /users/{userId}/events/{eventId} - конфликт возвращает 409")
    void updateEvent_Conflict_Returns409() throws Exception {
        // Given
        UpdateEventUserRequest updateRequest = new UpdateEventUserRequest();
        updateRequest.setTitle("Updated Title");

        when(eventService.updateEventByUser(eq(1L), eq(1L), any(UpdateEventUserRequest.class)))
                .thenThrow(new ConflictException("Нельзя изменить опубликованное событие"));

        // When/Then
        mockMvc.perform(patch("/users/1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is("CONFLICT")));
    }
}
