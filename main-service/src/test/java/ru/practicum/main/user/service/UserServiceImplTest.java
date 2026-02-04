package ru.practicum.main.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Pagination Validation Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

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
}
