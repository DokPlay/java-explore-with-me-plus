package ru.practicum.main.request.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.mapper.RequestMapper;
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.request.status.RequestStatus;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RequestService Validation Tests")
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestMapper requestMapper;

    @InjectMocks
    private RequestServiceImpl requestService;

    @Test
    @DisplayName("Должен выбросить ValidationException при null request body")
    void updateRequestStatus_NullRequest_ThrowsException() {
        assertThatThrownBy(() -> requestService.updateRequestStatus(1L, 1L, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("request body must not be null");
    }

    @Test
    @DisplayName("Должен выбросить ValidationException при null requestIds")
    void updateRequestStatus_NullRequestIds_ThrowsException() {
        EventRequestStatusUpdateRequest updateRequest = new EventRequestStatusUpdateRequest();
        updateRequest.setStatus(RequestStatus.CONFIRMED);

        assertThatThrownBy(() -> requestService.updateRequestStatus(1L, 1L, updateRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("requestIds must not be empty");
    }

    @Test
    @DisplayName("Должен выбросить ValidationException при пустом requestIds")
    void updateRequestStatus_EmptyRequestIds_ThrowsException() {
        EventRequestStatusUpdateRequest updateRequest = new EventRequestStatusUpdateRequest();
        updateRequest.setRequestIds(List.of());
        updateRequest.setStatus(RequestStatus.CONFIRMED);

        assertThatThrownBy(() -> requestService.updateRequestStatus(1L, 1L, updateRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("requestIds must not be empty");
    }

    @Test
    @DisplayName("Должен выбросить ValidationException при null status")
    void updateRequestStatus_NullStatus_ThrowsException() {
        EventRequestStatusUpdateRequest updateRequest = new EventRequestStatusUpdateRequest();
        updateRequest.setRequestIds(List.of(1L));

        assertThatThrownBy(() -> requestService.updateRequestStatus(1L, 1L, updateRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("status must be specified");
    }

    @Test
    @DisplayName("Должен выбросить ValidationException при null requestId в списке")
    void updateRequestStatus_NullRequestId_ThrowsException() {
        EventRequestStatusUpdateRequest updateRequest = new EventRequestStatusUpdateRequest();
        updateRequest.setRequestIds(Arrays.asList(1L, null));
        updateRequest.setStatus(RequestStatus.CONFIRMED);

        assertThatThrownBy(() -> requestService.updateRequestStatus(1L, 1L, updateRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("requestIds must not contain null");
    }

    @Test
    @DisplayName("Должен выбросить ValidationException при недопустимом status")
    void updateRequestStatus_InvalidStatus_ThrowsException() {
        EventRequestStatusUpdateRequest updateRequest = new EventRequestStatusUpdateRequest();
        updateRequest.setRequestIds(List.of(1L));
        updateRequest.setStatus(RequestStatus.CANCELED);

        assertThatThrownBy(() -> requestService.updateRequestStatus(1L, 1L, updateRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("status must be CONFIRMED or REJECTED");
    }

    @Test
    @DisplayName("Должен выбросить NotFoundException при отсутствии части заявок")
    void updateRequestStatus_MissingRequestIds_ThrowsException() {
        EventRequestStatusUpdateRequest updateRequest = new EventRequestStatusUpdateRequest();
        updateRequest.setRequestIds(List.of(1L, 2L));
        updateRequest.setStatus(RequestStatus.CONFIRMED);

        User initiator = new User();
        initiator.setId(1L);

        Event event = new Event();
        event.setId(1L);
        event.setInitiator(initiator);

        ParticipationRequest request = new ParticipationRequest();
        request.setId(1L);
        request.setEvent(event);
        request.setStatus(RequestStatus.PENDING);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findAllByIdIn(any())).thenReturn(List.of(request));

        assertThatThrownBy(() -> requestService.updateRequestStatus(1L, 1L, updateRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Заявки не найдены");
    }
}
