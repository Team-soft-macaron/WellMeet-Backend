package com.wellmeet.restaurant;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantRedisService {

    private final RedissonClient redissonClient;

    public void publish(String topicName, String restaurantId) {
        RTopic topic = redissonClient.getTopic(topicName);
        topic.publish(restaurantId);
    }
}
