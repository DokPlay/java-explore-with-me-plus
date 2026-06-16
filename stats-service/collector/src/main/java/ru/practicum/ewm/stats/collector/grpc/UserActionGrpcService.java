package ru.practicum.ewm.stats.collector.grpc;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.collector.kafka.UserActionProducer;
import ru.practicum.ewm.stats.proto.collector.UserActionControllerGrpc;
import ru.practicum.ewm.stats.proto.message.ActionTypeProto;
import ru.practicum.ewm.stats.proto.message.UserActionProto;

import java.time.Instant;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class UserActionGrpcService extends UserActionControllerGrpc.UserActionControllerImplBase {

    private final UserActionProducer producer;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        UserActionAvro action = UserActionAvro.newBuilder()
                .setUserId(request.getUserId())
                .setEventId(request.getEventId())
                .setActionType(toAvroAction(request.getActionType()))
                .setTimestamp(toInstant(request))
                .build();

        producer.send(action);
        log.debug("Collected user action: userId={}, eventId={}, action={}",
                action.getUserId(), action.getEventId(), action.getActionType());

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    private ActionTypeAvro toAvroAction(ActionTypeProto actionType) {
        return switch (actionType) {
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            case ACTION_VIEW, UNRECOGNIZED -> ActionTypeAvro.VIEW;
        };
    }

    private Instant toInstant(UserActionProto request) {
        Timestamp timestamp = request.hasTimestamp()
                ? request.getTimestamp()
                : Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build();
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
