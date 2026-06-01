package ru.practicum.main.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Pagination Validation Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("Должен выбросить ValidationException при некорректном size")
    void getUsers_InvalidSize_ThrowsException(int size) {
        assertThatThrownBy(() -> userService.getUsers(null, 0, size))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("size must be");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -10})
    @DisplayName("Должен выбросить ValidationException при некорректном from")
    void getUsers_InvalidFrom_ThrowsException(int from) {
        assertThatThrownBy(() -> userService.getUsers(null, from, 10))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("from must be");
    }

    @Test
    @DisplayName("Должен выбросить ValidationException при ids с null")
    void getUsers_NullIdInFilter_ThrowsException() {
        assertThatThrownBy(() -> userService.getUsers(java.util.Arrays.asList(1L, null), 0, 10))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("не должен содержать null");
    }

    @Test
    @DisplayName("Должен выбросить ConflictException при удалении пользователя с событиями")
    void delete_UserHasEvents_ThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(eventRepository.existsByInitiatorId(1L)).thenReturn(true);

        assertThatThrownBy(() -> userService.delete(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Нельзя удалить пользователя");
    }
}
