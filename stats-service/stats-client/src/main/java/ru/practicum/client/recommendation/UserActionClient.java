package ru.practicum.client.recommendation;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.proto.collector.UserActionControllerGrpc;
import ru.practicum.ewm.stats.proto.message.ActionTypeProto;
import ru.practicum.ewm.stats.proto.message.UserActionProto;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class UserActionClient {

    private static final int DEADLINE_SECONDS = 3;

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub collector;

    public void collectView(long userId, long eventId) {
        collect(userId, eventId, ActionTypeProto.ACTION_VIEW);
    }

    public void collectRegister(long userId, long eventId) {
        collect(userId, eventId, ActionTypeProto.ACTION_REGISTER);
    }

    public void collectLike(long userId, long eventId) {
        collect(userId, eventId, ActionTypeProto.ACTION_LIKE);
    }

    private void collect(long userId, long eventId, ActionTypeProto actionType) {
        UserActionProto request = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(actionType)
                .setTimestamp(toTimestamp(Instant.now()))
                .build();

        try {
            Empty ignored = collector
                    .withDeadlineAfter(DEADLINE_SECONDS, TimeUnit.SECONDS)
                    .collectUserAction(request);
        } catch (StatusRuntimeException e) {
            log.warn("Collector gRPC call failed: userId={}, eventId={}, action={}, status={}",
                    userId, eventId, actionType, e.getStatus());
        } catch (RuntimeException e) {
            log.warn("Collector gRPC call failed: userId={}, eventId={}, action={}, error={}",
                    userId, eventId, actionType, e.getMessage());
        }
    }

    private Timestamp toTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}
