package ru.practicum.main.location.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.location.model.ManagedLocation;

import java.util.Optional;

/**
 * Repository for managed locations.
 */
@Repository
public interface ManagedLocationRepository extends JpaRepository<ManagedLocation, Long> {

    /**
     * Checks whether location name already exists.
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Returns paginated active/inactive locations.
     */
    Page<ManagedLocation> findAllByActive(Boolean active, Pageable pageable);

    /**
     * Returns active location by id.
     */
    Optional<ManagedLocation> findByIdAndActiveTrue(Long id);
}
