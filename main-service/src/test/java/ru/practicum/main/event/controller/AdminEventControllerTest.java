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
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequest;
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
 * Integration тесты для AdminEventController.
 */
@WebMvcTest(AdminEventController.class)
@DisplayName("AdminEventController Tests")
class AdminEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    private ObjectMapper objectMapper;
    private EventFullDto eventFullDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

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
    }

    @Test
    @DisplayName("GET /admin/events - должен вернуть список событий")
    void getEvents_ReturnsEventList() throws Exception {
        // Given
        when(eventService.searchEventsForAdmin(
                any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(eventFullDto));

        // When/Then
        mockMvc.perform(get("/admin/events")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Event")));

        verify(eventService).searchEventsForAdmin(
                any(), any(), any(), any(), any(), eq(0), eq(10));
    }

    @Test
    @DisplayName("GET /admin/events - с фильтрами должен передать их в сервис")
    void getEvents_WithFilters_PassesFiltersToService() throws Exception {
        // Given
        when(eventService.searchEventsForAdmin(
                any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(eventFullDto));

        // When/Then
        mockMvc.perform(get("/admin/events")
                        .param("users", "1", "2")
                        .param("states", "PENDING", "PUBLISHED")
                        .param("categories", "1", "2")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(eventService).searchEventsForAdmin(
                eq(List.of(1L, 2L)),
                eq(List.of(EventState.PENDING, EventState.PUBLISHED)),
                eq(List.of(1L, 2L)),
                any(), any(), eq(0), eq(20));
    }

    @Test
    @DisplayName("GET /admin/events - пустой результат возвращает пустой массив")
    void getEvents_NoEvents_ReturnsEmptyArray() throws Exception {
        // Given
        when(eventService.searchEventsForAdmin(
                any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of());

        // When/Then
        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("PATCH /admin/events/{eventId} - должен опубликовать событие")
    void updateEvent_PublishEvent_ReturnsUpdatedEvent() throws Exception {
        // Given
        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setStateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);

        eventFullDto.setState(EventState.PUBLISHED);
        when(eventService.updateEventByAdmin(eq(1L), any(UpdateEventAdminRequest.class)))
                .thenReturn(eventFullDto);

        // When/Then
        mockMvc.perform(patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state", is("PUBLISHED")));

        verify(eventService).updateEventByAdmin(eq(1L), any(UpdateEventAdminRequest.class));
    }

    @Test
    @DisplayName("PATCH /admin/events/{eventId} - должен отклонить событие")
    void updateEvent_RejectEvent_ReturnsUpdatedEvent() throws Exception {
        // Given
        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setStateAction(UpdateEventAdminRequest.StateAction.REJECT_EVENT);

        eventFullDto.setState(EventState.CANCELED);
        when(eventService.updateEventByAdmin(eq(1L), any(UpdateEventAdminRequest.class)))
                .thenReturn(eventFullDto);

        // When/Then
        mockMvc.perform(patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state", is("CANCELED")));
    }

    @Test
    @DisplayName("PATCH /admin/events/{eventId} - событие не найдено возвращает 404")
    void updateEvent_NotFound_Returns404() throws Exception {
        // Given
        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setStateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);

        when(eventService.updateEventByAdmin(eq(999L), any(UpdateEventAdminRequest.class)))
                .thenThrow(new NotFoundException("Событие не найдено: id=999"));

        // When/Then
        mockMvc.perform(patch("/admin/events/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("NOT_FOUND")));
    }

    @Test
    @DisplayName("PATCH /admin/events/{eventId} - конфликт при публикации возвращает 409")
    void updateEvent_Conflict_Returns409() throws Exception {
        // Given
        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setStateAction(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT);

        when(eventService.updateEventByAdmin(eq(1L), any(UpdateEventAdminRequest.class)))
                .thenThrow(new ConflictException("Опубликовать можно только событие в статусе ожидания"));

        // When/Then
        mockMvc.perform(patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is("CONFLICT")));
    }

    @Test
    @DisplayName("PATCH /admin/events/{eventId} - обновление полей события")
    void updateEvent_UpdateFields_ReturnsUpdatedEvent() throws Exception {
        // Given
        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setTitle("Updated Admin Title");
        updateRequest.setAnnotation("Updated annotation for admin testing purposes");
        updateRequest.setPaid(true);

        eventFullDto.setTitle("Updated Admin Title");
        when(eventService.updateEventByAdmin(eq(1L), any(UpdateEventAdminRequest.class)))
                .thenReturn(eventFullDto);

        // When/Then
        mockMvc.perform(patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Admin Title")));
    }
}
