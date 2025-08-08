package com.wellmeet;

import com.wellmeet.domain.reservation.repository.ReservationRepository;
import com.wellmeet.domain.restaurant.availabledate.repository.AvailableDateRepository;
import com.wellmeet.fixture.AvailableDateGenerator;
import com.wellmeet.fixture.MemberGenerator;
import com.wellmeet.fixture.MenuGenerator;
import com.wellmeet.fixture.OwnerGenerator;
import com.wellmeet.fixture.ReservationGenerator;
import com.wellmeet.fixture.RestaurantGenerator;
import com.wellmeet.fixture.ReviewGenerator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(DataBaseCleaner.class)
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
