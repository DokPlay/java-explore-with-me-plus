package ru.practicum.main.subscription.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.subscription.dto.SubscriptionDto;
import ru.practicum.main.subscription.model.Subscription;

import java.util.List;

/**
 * Mapper for subscriptions.
 */
@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    /**
     * Converts entity to DTO.
     */
    @Mapping(target = "followerId", source = "follower.id")
    @Mapping(target = "followingId", source = "following.id")
    SubscriptionDto toDto(Subscription subscription);

    /**
     * Converts entities to DTO list.
     */
    List<SubscriptionDto> toDtoList(List<Subscription> subscriptions);
}
