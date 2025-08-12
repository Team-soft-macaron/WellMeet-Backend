package com.wellmeet.reservation;

import com.wellmeet.reservation.exception.ReservationErrorCode;
import com.wellmeet.reservation.exception.ReservationException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationRedisService {

    private final RedissonClient redissonClient;

    public void isReserving(String memberId, String restaurantId, Long availableDateId) {
        String key = String.format("reservation:%s:%s:%s", memberId, restaurantId, availableDateId);
        RBucket<String> bucket = redissonClient.getBucket(key);
        if (!bucket.setIfAbsent("1", Duration.ofSeconds(10))) {
            throw new ReservationException(ReservationErrorCode.ALREADY_RESERVING);
        }
    }

    public void isUpdating(String memberId, Long reservationId) {
        String key = String.format("reservation:update:%s:%s", memberId, reservationId);
        RBucket<String> bucket = redissonClient.getBucket(key);
        if (!bucket.setIfAbsent("1", Duration.ofSeconds(10))) {
            throw new ReservationException(ReservationErrorCode.ALREADY_RESERVING);
        }
    }

    public void deleteReservationLock() {
        redissonClient.getKeys().flushall();
    }
}
