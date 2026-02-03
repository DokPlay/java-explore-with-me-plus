package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.mapper.StatsMapper;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public void saveHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = statsMapper.toEntity(endpointHitDto);
        statsRepository.save(endpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       Boolean unique) {
        if (Boolean.TRUE.equals(unique)) {
            return statsRepository.findStatsUnique(start, end, uris);
        }
        return statsRepository.findStats(start, end, uris);
    }
}
