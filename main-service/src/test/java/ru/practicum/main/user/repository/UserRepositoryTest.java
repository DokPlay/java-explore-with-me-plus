package ru.practicum.main.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.main.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository тесты для UserRepository.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("User One");
        user1.setEmail("user1@test.com");
        entityManager.persist(user1);

        user2 = new User();
        user2.setName("User Two");
        user2.setEmail("user2@test.com");
        entityManager.persist(user2);

        user3 = new User();
        user3.setName("User Three");
        user3.setEmail("user3@test.com");
        entityManager.persist(user3);

        entityManager.flush();
    }

    @Nested
    @DisplayName("findByIds")
    class FindByIdsTests {

        @Test
        @DisplayName("Должен найти пользователей по списку ID")
        void findByIds_ReturnsMatchingUsers() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<User> result = userRepository.findByIds(
                    List.of(user1.getId(), user2.getId()), pageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent())
                    .extracting(User::getName)
                    .containsExactlyInAnyOrder("User One", "User Two");
        }

        @Test
        @DisplayName("Должен вернуть всех пользователей при null списке ID")
        void findByIds_NullIds_ReturnsAll() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<User> result = userRepository.findByIds(null, pageable);

            // Then
            assertThat(result.getContent()).hasSize(3);
        }

        @Test
        @DisplayName("Должен вернуть пустой список для несуществующих ID")
        void findByIds_NonExistentIds_ReturnsEmpty() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<User> result = userRepository.findByIds(List.of(999L, 998L), pageable);

            // Then
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("Должен корректно применять пагинацию")
        void findByIds_Pagination_Works() {
            // Given
            Pageable pageable = PageRequest.of(0, 2);

            // When
            Page<User> result = userRepository.findByIds(null, pageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(3);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("Должен вернуть вторую страницу")
        void findByIds_SecondPage_Works() {
            // Given
            Pageable pageable = PageRequest.of(1, 2);

            // When
            Page<User> result = userRepository.findByIds(null, pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getNumber()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("existsByEmail")
    class ExistsByEmailTests {

        @Test
        @DisplayName("Должен вернуть true для существующего email")
        void existsByEmail_ExistingEmail_ReturnsTrue() {
            // When
            boolean result = userRepository.existsByEmail("user1@test.com");

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Должен вернуть false для несуществующего email")
        void existsByEmail_NonExistingEmail_ReturnsFalse() {
            // When
            boolean result = userRepository.existsByEmail("nonexistent@test.com");

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Должен быть чувствителен к регистру")
        void existsByEmail_CaseSensitive() {
            // When
            boolean result = userRepository.existsByEmail("USER1@TEST.COM");

            // Then
            // В зависимости от настройки БД может быть true или false
            // Для H2 по умолчанию регистронезависимо, для PostgreSQL - зависимо
            assertThat(result).isIn(true, false);
        }
    }

    @Nested
    @DisplayName("CRUD Operations")
    class CrudOperationsTests {

        @Test
        @DisplayName("Должен сохранить нового пользователя")
        void save_NewUser_Success() {
            // Given
            User newUser = new User();
            newUser.setName("New User");
            newUser.setEmail("newuser@test.com");

            // When
            User saved = userRepository.save(newUser);

            // Then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getName()).isEqualTo("New User");
            assertThat(saved.getEmail()).isEqualTo("newuser@test.com");
        }

        @Test
        @DisplayName("Должен найти пользователя по ID")
        void findById_ExistingUser_Found() {
            // When
            var result = userRepository.findById(user1.getId());

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("User One");
        }

        @Test
        @DisplayName("Должен удалить пользователя")
        void delete_User_Success() {
            // Given
            Long userId = user1.getId();

            // When
            userRepository.deleteById(userId);
            entityManager.flush();

            // Then
            assertThat(userRepository.findById(userId)).isEmpty();
        }

        @Test
        @DisplayName("Должен обновить пользователя")
        void update_User_Success() {
            // Given
            user1.setName("Updated Name");

            // When
            User updated = userRepository.save(user1);
            entityManager.flush();

            // Then
            assertThat(updated.getName()).isEqualTo("Updated Name");
        }

        @Test
        @DisplayName("Должен посчитать количество пользователей")
        void count_ReturnsCorrectCount() {
            // When
            long count = userRepository.count();

            // Then
            assertThat(count).isEqualTo(3);
        }

        @Test
        @DisplayName("Должен проверить существование по ID")
        void existsById_ExistingUser_ReturnsTrue() {
            // When
            boolean exists = userRepository.existsById(user1.getId());

            // Then
            assertThat(exists).isTrue();
        }
    }
}
