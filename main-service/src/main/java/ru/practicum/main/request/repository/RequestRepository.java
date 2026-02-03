package ru.practicum.main.request.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.request.status.RequestStatus;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    // Найти все заявки пользователя
    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    // Найти все заявки на событие
    List<ParticipationRequest> findAllByEventId(Long eventId);

    // Проверить существование заявки пользователя на событие
    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    // Найти заявки по списку ID
    List<ParticipationRequest> findAllByIdIn(List<Long> ids);

    // Посчитать количество подтверждённых заявок на событие
    Long countByEventIdAndStatus(Long eventId, RequestStatus status);
}