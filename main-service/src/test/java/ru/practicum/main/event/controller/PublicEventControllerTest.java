package ru.practicum.main.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.service.EventService;
import ru.practicum.main.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration тесты для PublicEventController.
 */
@WebMvcTest(PublicEventController.class)
@DisplayName("PublicEventController Tests")
class PublicEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    private EventShortDto eventShortDto;
    private EventFullDto eventFullDto;

    @BeforeEach
    void setUp() {
        eventShortDto = EventShortDto.builder()
                .id(1L)
                .title("Test Event")
                .annotation("Test annotation for event testing")
                .eventDate(LocalDateTime.now().plusDays(7))
                .paid(false)
                .confirmedRequests(10L)
                .views(100L)
                .build();

        eventFullDto = EventFullDto.builder()
                .id(1L)
                .title("Test Event")
                .annotation("Test annotation for event testing")
                .description("Full description of the test event")
                .eventDate(LocalDateTime.now().plusDays(7))
                .state(EventState.PUBLISHED)
                .paid(false)
                .participantLimit(100)
                .confirmedRequests(10L)
                .views(100L)
                .build();
    }

    @Test
    @DisplayName("GET /events - должен вернуть список событий")
    void getEvents_ReturnsEventList() throws Exception {
        // Given
        when(eventService.searchPublicEvents(
                any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), any()))
                .thenReturn(List.of(eventShortDto));

        // When/Then
        mockMvc.perform(get("/events")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Event")));

        verify(eventService).searchPublicEvents(
                any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), any());
    }

    @Test
    @DisplayName("GET /events - с фильтрами должен передать их в сервис")
    void getEvents_WithFilters_PassesFiltersToService() throws Exception {
        // Given
        when(eventService.searchPublicEvents(
                any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), any()))
                .thenReturn(List.of(eventShortDto));

        // When/Then
        mockMvc.perform(get("/events")
                        .param("text", "test")
                        .param("categories", "1", "2")
                        .param("paid", "true")
                        .param("onlyAvailable", "true")
                        .param("sort", "EVENT_DATE")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(eventService).searchPublicEvents(
                eq("test"), eq(List.of(1L, 2L)), eq(true),
                any(), any(), eq(true), eq("EVENT_DATE"), eq(0), eq(20), any());
    }

    @Test
    @DisplayName("GET /events - пустой результат возвращает пустой массив")
    void getEvents_NoEvents_ReturnsEmptyArray() throws Exception {
        // Given
        when(eventService.searchPublicEvents(
                any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), any()))
                .thenReturn(List.of());

        // When/Then
        mockMvc.perform(get("/events")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /events/{id} - должен вернуть событие по ID")
    void getEventById_ReturnsEvent() throws Exception {
        // Given
        when(eventService.getPublishedEventById(eq(1L), any())).thenReturn(eventFullDto);

        // When/Then
        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Event")))
                .andExpect(jsonPath("$.state", is("PUBLISHED")));

        verify(eventService).getPublishedEventById(eq(1L), any());
    }

    @Test
    @DisplayName("GET /events/{id} - событие не найдено возвращает 404")
    void getEventById_NotFound_Returns404() throws Exception {
        // Given
        when(eventService.getPublishedEventById(eq(999L), any()))
                .thenThrow(new NotFoundException("Событие не найдено: id=999"));

        // When/Then
        mockMvc.perform(get("/events/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("NOT_FOUND")));
    }

    @Test
    @DisplayName("GET /events - значения по умолчанию для from и size")
    void getEvents_DefaultPagination() throws Exception {
        // Given
        when(eventService.searchPublicEvents(
                any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), any()))
                .thenReturn(List.of());

        // When/Then
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk());

        verify(eventService).searchPublicEvents(
                any(), any(), any(), any(), any(), any(), any(), eq(0), eq(10), any());
    }
}
