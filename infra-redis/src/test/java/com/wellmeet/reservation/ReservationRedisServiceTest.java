package com.wellmeet.reservation;

import static org.assertj.core.api.Assertions.*;

import com.wellmeet.reservation.exception.ReservationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("domain-redis-test")
class ReservationRedisServiceTest {

    @Autowired
    private ReservationRedisService reservationRedisService;

    @BeforeEach
    void setUp() {
        reservationRedisService.deleteReservationLock();
    }

    @Nested
    class IsReserving {

        @Test
        void 예약_락을_획득한다() {
            String memberId = "member-1";
            String restaurantId = "restaurant-1";
            Long availableDateId = 1L;

            assertThatCode(() ->
                    reservationRedisService.isReserving(memberId, restaurantId, availableDateId)
            ).doesNotThrowAnyException();
        }

        @Test
        void 동시_요청_시_하나만_락을_획득한다() throws InterruptedException {
            String memberId = "member-1";
            String restaurantId = "restaurant-1";
            Long availableDateId = 1L;

            int threadCount = 10;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        reservationRedisService.isReserving(memberId, restaurantId, availableDateId);
                        successCount.incrementAndGet();
                    } catch (ReservationException e) {
                        failCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executorService.shutdown();

            assertThat(successCount.get()).isEqualTo(1);
            assertThat(failCount.get()).isEqualTo(9);
        }

        @Test
        void 이미_락을_획득한_경우_예외가_발생한다() {
            String memberId = "member-1";
            String restaurantId = "restaurant-1";
            Long availableDateId = 1L;

            reservationRedisService.isReserving(memberId, restaurantId, availableDateId);

            assertThatThrownBy(() ->
                    reservationRedisService.isReserving(memberId, restaurantId, availableDateId)
            ).isInstanceOf(ReservationException.class);
        }

        @Test
        void 다른_회원은_같은_예약에_대해_락을_획득할_수_있다() {
            String memberId1 = "member-1";
            String memberId2 = "member-2";
            String restaurantId = "restaurant-1";
            Long availableDateId = 1L;

            reservationRedisService.isReserving(memberId1, restaurantId, availableDateId);

            assertThatCode(() ->
                    reservationRedisService.isReserving(memberId2, restaurantId, availableDateId)
            ).doesNotThrowAnyException();
        }
    }

    @Nested
    class IsUpdating {

        @Test
        void 예약_수정_락을_획득한다() {
            String memberId = "member-1";
            Long reservationId = 1L;

            assertThatCode(() ->
                    reservationRedisService.isUpdating(memberId, reservationId)
            ).doesNotThrowAnyException();
        }

        @Test
        void 동시_수정_요청_시_하나만_락을_획득한다() throws InterruptedException {
            String memberId = "member-1";
            Long reservationId = 1L;

            int threadCount = 10;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        reservationRedisService.isUpdating(memberId, reservationId);
                        successCount.incrementAndGet();
                    } catch (ReservationException e) {
                        failCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executorService.shutdown();

            assertThat(successCount.get()).isEqualTo(1);
            assertThat(failCount.get()).isEqualTo(9);
        }

        @Test
        void 이미_락을_획득한_경우_예외가_발생한다() {
            String memberId = "member-1";
            Long reservationId = 1L;

            reservationRedisService.isUpdating(memberId, reservationId);

            assertThatThrownBy(() ->
                    reservationRedisService.isUpdating(memberId, reservationId)
            ).isInstanceOf(ReservationException.class);
        }
    }

    @Nested
    class DeleteReservationLock {

        @Test
        void 락_삭제_후_다시_획득_가능하다() {
            String memberId = "member-1";
            String restaurantId = "restaurant-1";
            Long availableDateId = 1L;

            reservationRedisService.isReserving(memberId, restaurantId, availableDateId);
            reservationRedisService.deleteReservationLock();

            assertThatCode(() ->
                    reservationRedisService.isReserving(memberId, restaurantId, availableDateId)
            ).doesNotThrowAnyException();
        }
    }
}
