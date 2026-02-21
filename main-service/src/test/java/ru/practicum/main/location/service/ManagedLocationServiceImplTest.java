package ru.practicum.main.location.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.location.dto.ManagedLocationDto;
import ru.practicum.main.location.dto.NewManagedLocationDto;
import ru.practicum.main.location.dto.UpdateManagedLocationDto;
import ru.practicum.main.location.mapper.ManagedLocationMapper;
import ru.practicum.main.location.model.ManagedLocation;
import ru.practicum.main.location.repository.ManagedLocationRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ManagedLocationServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ManagedLocationService Unit Tests")
class ManagedLocationServiceImplTest {

    @Mock
    private ManagedLocationRepository managedLocationRepository;

    @Mock
    private ManagedLocationMapper managedLocationMapper;

    @InjectMocks
    private ManagedLocationServiceImpl managedLocationService;

    @Nested
    @DisplayName("create/update/delete")
    class MutationTests {

        @Test
        @DisplayName("Должен создать локацию")
        void createLocation_success() {
            NewManagedLocationDto request = NewManagedLocationDto.builder()
                    .name("Moscow center")
                    .lat(55.75)
                    .lon(37.61)
                    .build();
            ManagedLocation saved = ManagedLocation.builder().id(1L).name("Moscow center").active(true).build();
            ManagedLocationDto dto = ManagedLocationDto.builder().id(1L).name("Moscow center").active(true).build();

            when(managedLocationRepository.existsByNameIgnoreCase("Moscow center")).thenReturn(false);
            when(managedLocationRepository.save(any(ManagedLocation.class))).thenReturn(saved);
            when(managedLocationMapper.toDto(saved)).thenReturn(dto);

            ManagedLocationDto result = managedLocationService.createLocation(request);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Moscow center");
        }

        @Test
        @DisplayName("Должен выбросить ConflictException при дубликате имени")
        void createLocation_duplicateName_throwsConflict() {
            NewManagedLocationDto request = NewManagedLocationDto.builder()
                    .name("Moscow center")
                    .lat(55.75)
                    .lon(37.61)
                    .build();
            when(managedLocationRepository.existsByNameIgnoreCase("Moscow center")).thenReturn(true);

            assertThatThrownBy(() -> managedLocationService.createLocation(request))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("уже существует");
        }

        @Test
        @DisplayName("Должен обновить локацию")
        void updateLocation_success() {
            ManagedLocation location = ManagedLocation.builder()
                    .id(1L)
                    .name("Old")
                    .lat(55.0)
                    .lon(37.0)
                    .active(true)
                    .build();
            UpdateManagedLocationDto request = UpdateManagedLocationDto.builder()
                    .name("New")
                    .lat(56.0)
                    .active(false)
                    .build();

            when(managedLocationRepository.findById(1L)).thenReturn(Optional.of(location));
            when(managedLocationRepository.existsByNameIgnoreCase("New")).thenReturn(false);
            when(managedLocationRepository.save(any(ManagedLocation.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(managedLocationMapper.toDto(any(ManagedLocation.class)))
                    .thenReturn(ManagedLocationDto.builder().id(1L).name("New").active(false).build());

            ManagedLocationDto result = managedLocationService.updateLocation(1L, request);

            assertThat(result.getName()).isEqualTo("New");
            assertThat(result.getActive()).isFalse();
        }

        @Test
        @DisplayName("Должен сделать soft-delete локации")
        void deleteLocation_success() {
            ManagedLocation location = ManagedLocation.builder().id(1L).active(true).build();

            when(managedLocationRepository.findById(1L)).thenReturn(Optional.of(location));
            when(managedLocationRepository.save(any(ManagedLocation.class))).thenAnswer(invocation -> invocation.getArgument(0));

            managedLocationService.deleteLocation(1L);

            ArgumentCaptor<ManagedLocation> captor = ArgumentCaptor.forClass(ManagedLocation.class);
            verify(managedLocationRepository).save(captor.capture());
            assertThat(captor.getValue().getActive()).isFalse();
        }

        @Test
        @DisplayName("Повторный delete inactive локации не должен сохранять")
        void deleteLocation_alreadyInactive_noSave() {
            ManagedLocation location = ManagedLocation.builder().id(1L).active(false).build();
            when(managedLocationRepository.findById(1L)).thenReturn(Optional.of(location));

            managedLocationService.deleteLocation(1L);

            verify(managedLocationRepository, never()).save(any(ManagedLocation.class));
        }
    }

    @Nested
    @DisplayName("read operations")
    class ReadTests {

        @Test
        @DisplayName("Должен вернуть локации для админа")
        void getLocations_admin_success() {
            when(managedLocationRepository.findAll(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(ManagedLocation.builder().id(1L).build())));
            when(managedLocationMapper.toDtoList(any()))
                    .thenReturn(List.of(ManagedLocationDto.builder().id(1L).name("Moscow").build()));

            List<ManagedLocationDto> result = managedLocationService.getLocations(null, 0, 10);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Должен вернуть активную локацию публично")
        void getPublicLocationById_success() {
            when(managedLocationRepository.findByIdAndActiveTrue(1L))
                    .thenReturn(Optional.of(ManagedLocation.builder().id(1L).name("Moscow").build()));
            when(managedLocationMapper.toDto(any(ManagedLocation.class)))
                    .thenReturn(ManagedLocationDto.builder().id(1L).name("Moscow").active(true).build());

            ManagedLocationDto result = managedLocationService.getPublicLocationById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Должен выбросить ValidationException при плохой пагинации")
        void getLocations_invalidPagination_throwsValidation() {
            assertThatThrownBy(() -> managedLocationService.getLocations(null, 0, 0))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("size must be");
        }

        @Test
        @DisplayName("Должен выбросить NotFoundException для несуществующей public локации")
        void getPublicLocationById_notFound_throwsNotFound() {
            when(managedLocationRepository.findByIdAndActiveTrue(anyLong())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> managedLocationService.getPublicLocationById(1L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Активная локация не найдена");
        }
    }
}
