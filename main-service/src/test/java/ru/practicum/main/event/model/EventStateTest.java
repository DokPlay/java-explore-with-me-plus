package ru.practicum.main.event.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link EventState} enum.
 */
@DisplayName("EventState Enum Tests")
class EventStateTest {

    @Test
    @DisplayName("Должен иметь значение PENDING")
    void pending_Exists() {
        // Action and assert
        assertThat(EventState.PENDING).isNotNull();
        assertThat(EventState.PENDING.name()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("Должен иметь значение PUBLISHED")
    void published_Exists() {
        // Action and assert
        assertThat(EventState.PUBLISHED).isNotNull();
        assertThat(EventState.PUBLISHED.name()).isEqualTo("PUBLISHED");
    }

    @Test
    @DisplayName("Должен иметь значение CANCELED")
    void canceled_Exists() {
        // Action and assert
        assertThat(EventState.CANCELED).isNotNull();
        assertThat(EventState.CANCELED.name()).isEqualTo("CANCELED");
    }

    @Test
    @DisplayName("Должен содержать 3 значения")
    void allValues_Count() {
        // Action and assert
        assertThat(EventState.values()).hasSize(3);
    }

    @Test
    @DisplayName("valueOf должен работать корректно")
    void valueOf_Works() {
        // Action and assert
        assertThat(EventState.valueOf("PENDING")).isEqualTo(EventState.PENDING);
        assertThat(EventState.valueOf("PUBLISHED")).isEqualTo(EventState.PUBLISHED);
        assertThat(EventState.valueOf("CANCELED")).isEqualTo(EventState.CANCELED);
    }

    @Test
    @DisplayName("ordinal должен быть последовательным")
    void ordinal_IsSequential() {
        // Action and assert
        assertThat(EventState.PENDING.ordinal()).isGreaterThanOrEqualTo(0);
        assertThat(EventState.PUBLISHED.ordinal()).isGreaterThan(EventState.PENDING.ordinal());
        assertThat(EventState.CANCELED.ordinal()).isGreaterThan(EventState.PUBLISHED.ordinal());
    }
}
