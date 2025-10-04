package com.wellmeet;

import com.wellmeet.domain.reservation.repository.ReservationRepository;
import com.wellmeet.domain.restaurant.availabledate.repository.AvailableDateRepository;
import com.wellmeet.domain.fixture.AvailableDateGenerator;
import com.wellmeet.domain.fixture.MemberGenerator;
import com.wellmeet.domain.fixture.MenuGenerator;
import com.wellmeet.domain.fixture.OwnerGenerator;
import com.wellmeet.domain.fixture.ReservationGenerator;
import com.wellmeet.domain.fixture.RestaurantGenerator;
import com.wellmeet.domain.fixture.ReviewGenerator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(DataBaseCleaner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class BaseServiceTest {

    @Autowired
    protected AvailableDateGenerator availableDateGenerator;

    @Autowired
    protected ReservationGenerator reservationGenerator;

    @Autowired
    protected MemberGenerator memberGenerator;

    @Autowired
    protected OwnerGenerator ownerGenerator;

    @Autowired
    protected RestaurantGenerator restaurantGenerator;

    @Autowired
    protected MenuGenerator menuGenerator;

    @Autowired
    protected ReviewGenerator reviewGenerator;

    @Autowired
    protected ReservationRepository reservationRepository;

    @Autowired
    protected AvailableDateRepository availableDateRepository;

    protected void runAtSameTime(int count, Runnable task) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            executorService.submit(() -> {
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();
    }

    protected void runAtSameTime(List<Runnable> tasks) throws InterruptedException {
        int size = tasks.size();
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        CountDownLatch latch = new CountDownLatch(size);
        for (Runnable task : tasks) {
            executorService.submit(() -> {
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();
    }
}
