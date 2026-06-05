package ru.practicum.main.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.user.dto.NewUserRequest;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;
import ru.practicum.main.util.PaginationValidator;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@Validated
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public UserDto create(NewUserRequest request) {
        log.info("Создание нового пользователя: email={}, name={}",
                request.getEmail(), request.getName());
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Попытка создать пользователя с уже существующим email: {}", request.getEmail());
            throw new ConflictException("Пользователь с email '" + request.getEmail() + "' уже существует");
        }

        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .build();

        return toDto(userRepository.save(user));
    }


    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        PaginationValidator.validatePagination(from, size);
        validateIdsFilter(ids);
        if (ids != null && ids.isEmpty()) {
            return List.of();
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        List<Long> idsFilter = (ids == null || ids.isEmpty()) ? null : ids;

        return userRepository.findByIds(idsFilter, pageable)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        if (eventRepository.existsByInitiatorId(userId)) {
            throw new ConflictException("Нельзя удалить пользователя с опубликованными или созданными событиями");
        }
        userRepository.deleteById(userId);
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    private void validateIdsFilter(List<Long> ids) {
        if (ids == null) {
            return;
        }
        if (ids.stream().anyMatch(Objects::isNull)) {
            throw new ValidationException("Список ids не должен содержать null");
        }
        if (ids.stream().anyMatch(id -> id <= 0)) {
            throw new ValidationException("Идентификаторы в списке ids должны быть больше 0");
        }
    }
}
