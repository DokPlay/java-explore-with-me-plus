package ru.practicum.main.event.repository;

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
import ru.practicum.main.category.model.Category;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.EventState;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository тесты для EventRepository.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("EventRepository Tests")
class EventRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EventRepository eventRepository;

    private User user1;
    private User user2;
    private Category category1;
    private Category category2;
    private Location location;
    private Event event1;
    private Event event2;
    private Event event3;

    @BeforeEach
    void setUp() {
        // Создаём пользователей
        user1 = new User();
        user1.setName("User One");
        user1.setEmail("user1@test.com");
        entityManager.persist(user1);

        user2 = new User();
        user2.setName("User Two");
        user2.setEmail("user2@test.com");
        entityManager.persist(user2);

        // Создаём категории
        category1 = new Category();
        category1.setName("Category One");
        entityManager.persist(category1);

        category2 = new Category();
        category2.setName("Category Two");
        entityManager.persist(category2);

        // Создаём локацию
        location = new Location();
        location.setLat(55.75f);
        location.setLon(37.62f);
        entityManager.persist(location);

        // Создаём события
        event1 = createEvent("Event One", "First event annotation text",
                user1, category1, EventState.PUBLISHED, LocalDateTime.now().plusDays(5), true);
        entityManager.persist(event1);

        event2 = createEvent("Event Two", "Second event annotation text",
                user1, category2, EventState.PENDING, LocalDateTime.now().plusDays(10), false);
        entityManager.persist(event2);

        event3 = createEvent("Event Three", "Third event annotation text",
                user2, category1, EventState.PUBLISHED, LocalDateTime.now().plusDays(3), true);
        entityManager.persist(event3);

        entityManager.flush();
    }

    private Event createEvent(String title, String annotation, User initiator,
                               Category category, EventState state, LocalDateTime eventDate, boolean paid) {
        Event event = new Event();
        event.setTitle(title);
        event.setAnnotation(annotation);
        event.setDescription("Description for " + title);
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setLocation(location);
        event.setState(state);
        event.setEventDate(eventDate);
        event.setCreatedOn(LocalDateTime.now());
        event.setPaid(paid);
        event.setParticipantLimit(100);
        event.setRequestModeration(true);
        event.setConfirmedRequests(0L);
        event.setViews(0L);
        if (state == EventState.PUBLISHED) {
            event.setPublishedOn(LocalDateTime.now());
        }
        return event;
    }

    @Nested
    @DisplayName("findAllByInitiatorId")
    class FindAllByInitiatorIdTests {

        @Test
        @DisplayName("Должен найти все события пользователя")
        void findAllByInitiatorId_ReturnsUserEvents() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Event> result = eventRepository.findAllByInitiatorId(user1.getId(), pageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent())
                    .extracting(Event::getTitle)
                    .containsExactlyInAnyOrder("Event One", "Event Two");
        }

        @Test
        @DisplayName("Должен вернуть пустой список если у пользователя нет событий")
        void findAllByInitiatorId_NoEvents_ReturnsEmpty() {
            // Given
            User newUser = new User();
            newUser.setName("New User");
            newUser.setEmail("newuser@test.com");
            entityManager.persist(newUser);
            entityManager.flush();

            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Event> result = eventRepository.findAllByInitiatorId(newUser.getId(), pageable);

            // Then
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("Должен корректно применять пагинацию")
        void findAllByInitiatorId_Pagination_Works() {
            // Given
            Pageable pageable = PageRequest.of(0, 1);

            // When
            Page<Event> result = eventRepository.findAllByInitiatorId(user1.getId(), pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("findByIdAndInitiatorId")
    class FindByIdAndInitiatorIdTests {

        @Test
        @DisplayName("Должен найти событие по ID и ID пользователя")
        void findByIdAndInitiatorId_Found() {
            // When
            Optional<Event> result = eventRepository.findByIdAndInitiatorId(event1.getId(), user1.getId());

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getTitle()).isEqualTo("Event One");
        }

        @Test
        @DisplayName("Не должен найти событие другого пользователя")
        void findByIdAndInitiatorId_WrongUser_NotFound() {
            // When
            Optional<Event> result = eventRepository.findByIdAndInitiatorId(event1.getId(), user2.getId());

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Не должен найти несуществующее событие")
        void findByIdAndInitiatorId_WrongEventId_NotFound() {
            // When
            Optional<Event> result = eventRepository.findByIdAndInitiatorId(999L, user1.getId());

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByIdAndState")
    class FindByIdAndStateTests {

        @Test
        @DisplayName("Должен найти опубликованное событие")
        void findByIdAndState_Published_Found() {
            // When
            Optional<Event> result = eventRepository.findByIdAndState(event1.getId(), EventState.PUBLISHED);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getState()).isEqualTo(EventState.PUBLISHED);
        }

        @Test
        @DisplayName("Не должен найти неопубликованное событие по статусу PUBLISHED")
        void findByIdAndState_NotPublished_NotFound() {
            // When
            Optional<Event> result = eventRepository.findByIdAndState(event2.getId(), EventState.PUBLISHED);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findEventsForAdmin")
    class FindEventsForAdminTests {

        @Test
        @DisplayName("Должен найти все события без фильтров")
        void findEventsForAdmin_NoFilters_ReturnsAll() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Event> result = eventRepository.findEventsForAdmin(
                    null, null, null, null, null, pageable);

            // Then
            assertThat(result.getContent()).hasSize(3);
        }

        @Test
        @DisplayName("Должен отфильтровать по пользователям")
        void findEventsForAdmin_FilterByUsers() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Event> result = eventRepository.findEventsForAdmin(
                    List.of(user1.getId()), null, null, null, null, pageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent())
                    .extracting(e -> e.getInitiator().getId())
                    .containsOnly(user1.getId());
        }

        @Test
        @DisplayName("Должен отфильтровать по статусам")
        void findEventsForAdmin_FilterByStates() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Event> result = eventRepository.findEventsForAdmin(
                    null, List.of(EventState.PUBLISHED), null, null, null, pageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent())
                    .extracting(Event::getState)
                    .containsOnly(EventState.PUBLISHED);
        }

        @Test
        @DisplayName("Должен отфильтровать по категориям")
        void findEventsForAdmin_FilterByCategories() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Event> result = eventRepository.findEventsForAdmin(
                    null, null, List.of(category1.getId()), null, null, pageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent())
                    .extracting(e -> e.getCategory().getId())
                    .containsOnly(category1.getId());
        }

        @Test
        @DisplayName("Должен отфильтровать по диапазону дат")
        void findEventsForAdmin_FilterByDateRange() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            LocalDateTime rangeStart = LocalDateTime.now().plusDays(4);
            LocalDateTime rangeEnd = LocalDateTime.now().plusDays(8);

            // When
            Page<Event> result = eventRepository.findEventsForAdmin(
                    null, null, null, rangeStart, rangeEnd, pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("Event One");
        }

        @Test
        @DisplayName("Должен применить все фильтры одновременно")
        void findEventsForAdmin_AllFilters() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Event> result = eventRepository.findEventsForAdmin(
                    List.of(user1.getId()),
                    List.of(EventState.PUBLISHED),
                    List.of(category1.getId()),
                    LocalDateTime.now(),
                    LocalDateTime.now().plusDays(10),
                    pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("Event One");
        }
    }

    @Nested
    @DisplayName("findPublicEvents")
    class FindPublicEventsTests {

        @Test
        @DisplayName("Должен найти только опубликованные события")
        void findPublicEvents_OnlyPublished() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Event> result = eventRepository.findPublicEvents(
                    null, null, null, LocalDateTime.now(), null, false, pageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent())
                    .extracting(Event::getState)
                    .containsOnly(EventState.PUBLISHED);
        }

        @Test
        @DisplayName("Должен искать по тексту в аннотации и описании")
        void findPublicEvents_TextSearch() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Event> result = eventRepository.findPublicEvents(
                    "First", null, null, LocalDateTime.now(), null, false, pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("Event One");
        }

        @Test
        @DisplayName("Должен отфильтровать по платным событиям")
        void findPublicEvents_FilterByPaid() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Event> result = eventRepository.findPublicEvents(
                    null, null, true, LocalDateTime.now(), null, false, pageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent())
                    .extracting(Event::getPaid)
                    .containsOnly(true);
        }

        @Test
        @DisplayName("Должен отфильтровать по категориям")
        void findPublicEvents_FilterByCategories() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Event> result = eventRepository.findPublicEvents(
                    null, List.of(category1.getId()), null, LocalDateTime.now(), null, false, pageable);

            // Then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent())
                    .extracting(e -> e.getCategory().getId())
                    .containsOnly(category1.getId());
        }
    }
}
