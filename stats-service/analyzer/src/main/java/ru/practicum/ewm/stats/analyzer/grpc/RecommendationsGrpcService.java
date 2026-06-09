package ru.practicum.ewm.stats.analyzer.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.analyzer.service.RecommendationService;
import ru.practicum.ewm.stats.analyzer.service.RecommendedEvent;
import ru.practicum.ewm.stats.proto.dashboard.RecommendationsControllerGrpc;
import ru.practicum.ewm.stats.proto.message.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.proto.message.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.message.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.proto.message.UserPredictionsRequestProto;

@GrpcService
@RequiredArgsConstructor
public class RecommendationsGrpcService extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final RecommendationService recommendationService;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        recommendationService.getRecommendationsForUser(request.getUserId(), request.getMaxResults())
                .forEach(recommended -> responseObserver.onNext(toProto(recommended)));
        responseObserver.onCompleted();
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request,
                                 StreamObserver<RecommendedEventProto> responseObserver) {
        recommendationService.getSimilarEvents(request.getEventId(), request.getUserId(), request.getMaxResults())
                .forEach(recommended -> responseObserver.onNext(toProto(recommended)));
        responseObserver.onCompleted();
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        recommendationService.getInteractionsCount(request.getEventIdList())
                .forEach(recommended -> responseObserver.onNext(toProto(recommended)));
        responseObserver.onCompleted();
    }

    private RecommendedEventProto toProto(RecommendedEvent recommended) {
        return RecommendedEventProto.newBuilder()
                .setEventId(recommended.eventId())
                .setScore(recommended.score())
                .build();
    }
}
