package ru.practicum.client.recommendation;

import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.client.recommendation.dto.RecommendedEventDto;
import ru.practicum.ewm.stats.proto.dashboard.RecommendationsControllerGrpc;
import ru.practicum.ewm.stats.proto.message.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.proto.message.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.message.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.proto.message.UserPredictionsRequestProto;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterators;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
@Slf4j
public class RecommendationsClient {

    private static final int DEADLINE_SECONDS = 3;

    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub analyzer;

    public List<RecommendedEventDto> getRecommendationsForUser(long userId, int maxResults) {
        UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        return call(() -> analyzer
                .withDeadlineAfter(DEADLINE_SECONDS, TimeUnit.SECONDS)
                .getRecommendationsForUser(request));
    }

    public List<RecommendedEventDto> getSimilarEvents(long eventId, long userId, int maxResults) {
        SimilarEventsRequestProto request = SimilarEventsRequestProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        return call(() -> analyzer
                .withDeadlineAfter(DEADLINE_SECONDS, TimeUnit.SECONDS)
                .getSimilarEvents(request));
    }

    public Map<Long, Double> getInteractionsCount(Collection<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Map.of();
        }

        InteractionsCountRequestProto request = InteractionsCountRequestProto.newBuilder()
                .addAllEventId(eventIds)
                .build();

        return call(() -> analyzer
                .withDeadlineAfter(DEADLINE_SECONDS, TimeUnit.SECONDS)
                .getInteractionsCount(request))
                .stream()
                .collect(Collectors.toMap(
                        RecommendedEventDto::eventId,
                        RecommendedEventDto::score,
                        (first, second) -> first
                ));
    }

    private List<RecommendedEventDto> call(GrpcStreamSupplier supplier) {
        try {
            return asStream(supplier.get())
                    .map(this::toDto)
                    .toList();
        } catch (StatusRuntimeException e) {
            log.warn("Analyzer gRPC call failed: status={}", e.getStatus());
            return List.of();
        } catch (RuntimeException e) {
            log.warn("Analyzer gRPC call failed: error={}", e.getMessage());
            return List.of();
        }
    }

    private RecommendedEventDto toDto(RecommendedEventProto proto) {
        return new RecommendedEventDto(proto.getEventId(), proto.getScore());
    }

    private Stream<RecommendedEventProto> asStream(Iterator<RecommendedEventProto> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, 0),
                false
        );
    }

    @FunctionalInterface
    private interface GrpcStreamSupplier {
        Iterator<RecommendedEventProto> get();
    }
}
