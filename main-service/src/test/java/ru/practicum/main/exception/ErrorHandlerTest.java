package ru.practicum.main.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.main.event.controller.PublicEventController;
import ru.practicum.main.event.service.EventService;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для ErrorHandler.
 */
@WebMvcTest(PublicEventController.class)
@DisplayName("ErrorHandler Tests")
class ErrorHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Test
    @DisplayName("NotFoundException должен возвращать 404 с корректным телом")
    void handleNotFoundException_Returns404() throws Exception {
        // Given
        when(eventService.getPublishedEventById(any(), any()))
                .thenThrow(new NotFoundException("Событие не найдено: id=999"));

        // When/Then
        mockMvc.perform(get("/events/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.reason", is("The required object was not found.")))
                .andExpect(jsonPath("$.message", containsString("Событие не найдено")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("ValidationException должен возвращать 400 с корректным телом")
    void handleValidationException_Returns400() throws Exception {
        // Given
        when(eventService.searchPublicEvents(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new ValidationException("Дата начала не может быть после даты окончания"));

        // When/Then
        mockMvc.perform(get("/events")
                        .param("rangeStart", "2025-01-01 00:00:00")
                        .param("rangeEnd", "2024-01-01 00:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", containsString("Дата начала")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("ConflictException должен возвращать 409 с корректным телом")
    void handleConflictException_Returns409() throws Exception {
        // Given
        when(eventService.getPublishedEventById(any(), any()))
                .thenThrow(new ConflictException("Конфликт при обработке запроса"));

        // When/Then
        mockMvc.perform(get("/events/1"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("CONFLICT")))
                .andExpect(jsonPath("$.reason", is("For the requested operation the conditions are not met.")))
                .andExpect(jsonPath("$.message", containsString("Конфликт")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("RuntimeException должен возвращать 500")
    void handleGenericException_Returns500() throws Exception {
        // Given
        when(eventService.getPublishedEventById(any(), any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When/Then
        mockMvc.perform(get("/events/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("INTERNAL_SERVER_ERROR")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}
