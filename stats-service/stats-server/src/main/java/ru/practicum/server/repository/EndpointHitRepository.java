package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.model.EndpointHit;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
}
