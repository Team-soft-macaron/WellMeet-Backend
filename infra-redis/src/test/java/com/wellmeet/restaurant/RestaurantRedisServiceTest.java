package com.wellmeet.restaurant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("infra-redis-test")
class RestaurantRedisServiceTest {

    @Autowired
    private RestaurantRedisService restaurantRedisService;

    @Autowired
    private RedissonClient redissonClient;

    @Nested
    class Publish {

        @Test
        void 토픽에_메시지를_발행한다() throws InterruptedException {
            String topicName = "test-topic";
            String restaurantId = "restaurant-1";
            CountDownLatch latch = new CountDownLatch(1);
            AtomicInteger messageCount = new AtomicInteger(0);

            RTopic topic = redissonClient.getTopic(topicName);
            topic.addListener(String.class, (channel, msg) -> {
                if (msg.equals(restaurantId)) {
                    messageCount.incrementAndGet();
                }
                latch.countDown();
            });

            restaurantRedisService.publish(topicName, restaurantId);

            boolean received = latch.await(5, TimeUnit.SECONDS);

            assertThat(received).isTrue();
            assertThat(messageCount.get()).isEqualTo(1);
        }

        @Test
        void 여러_메시지를_발행할_수_있다() throws InterruptedException {
            String topicName = "multi-topic";
            int messageCount = 5;
            CountDownLatch latch = new CountDownLatch(messageCount);
            AtomicInteger receivedCount = new AtomicInteger(0);

            RTopic topic = redissonClient.getTopic(topicName);
            topic.addListener(String.class, (channel, msg) -> {
                receivedCount.incrementAndGet();
                latch.countDown();
            });

            for (int i = 0; i < messageCount; i++) {
                restaurantRedisService.publish(topicName, "restaurant-" + i);
            }

            boolean received = latch.await(5, TimeUnit.SECONDS);

            assertThat(received).isTrue();
            assertThat(receivedCount.get()).isEqualTo(messageCount);
        }

        @Test
        void 구독자가_없어도_메시지를_발행할_수_있다() {
            String topicName = "no-subscriber-topic";
            String restaurantId = "restaurant-1";

            assertThatCode(() ->
                    restaurantRedisService.publish(topicName, restaurantId)
            ).doesNotThrowAnyException();
        }
    }
}
