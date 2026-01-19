package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {
    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;
    
    @Transactional
    public void saveHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = statsMapper.toEntity(endpointHitDto);
        statsRepository.save(endpointHit);
    }
    
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, 
                                       List<String> uris, Boolean unique) {
        if (Boolean.TRUE.equals(unique)) {
            return statsRepository.findStatsUnique(start, end, uris);
        } else {
            return statsRepository.findStats(start, end, uris);
        }
    }
}