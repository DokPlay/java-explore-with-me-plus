package ru.practicum.main.request.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.request.status.RequestStatus;
import ru.practicum.main.user.model.User;
import java.time.LocalDateTime;
import  ru.practicum.main.event.model.Event;

@Entity
@Table(name = "participation_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                     // ID заявки

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;                 // Событие, на которое подана заявка

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;              // Пользователь, подавший заявку

    private LocalDateTime created;       // Дата создания заявки

    @Enumerated(EnumType.STRING)
    private RequestStatus status;        // Статус: PENDING, CONFIRMED, REJECTED, CANCELED
}

