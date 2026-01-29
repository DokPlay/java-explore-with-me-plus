package ru.practicum.main.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.main.user.model.User;

import java.util.List;

/**
 * Репозиторий для работы с пользователями.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Найти пользователей по списку ID.
     */
    @Query("SELECT u FROM User u WHERE (:ids IS NULL OR u.id IN :ids)")
    Page<User> findByIds(@Param("ids") List<Long> ids, Pageable pageable);

    /**
     * Проверить существование пользователя по email.
     */
    boolean existsByEmail(String email);
}
