package com.wellmeet.restaurant;

import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RestaurantRedisService {
    private final RedissonClient redissonClient;
    public void publish(String topicName, String restaurantId){
        RTopic topic = redissonClient.getTopic(topicName);
        topic.publish(restaurantId);
    }
}
