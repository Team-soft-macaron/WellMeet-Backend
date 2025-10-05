package com.wellmeet.batch.job;

import com.wellmeet.batch.entity.FailedNotification;
import com.wellmeet.batch.repository.FailedNotificationRepository;
import com.wellmeet.kafka.dto.payload.ReservationReminderPayload;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSkipListener implements SkipListener<ReservationReminderPayload, ReservationReminderPayload> {

    private final FailedNotificationRepository failedNotificationRepository;
    private final Clock clock;

    @Override
    public void onSkipInRead(Throwable throwable) {
        log.error("Skipped item during read", throwable);
    }

    @Override
    public void onSkipInProcess(ReservationReminderPayload item, Throwable throwable) {
        log.error("Skipped item during process: reservationId={}", item.getReservationId(), throwable);
    }

    @Override
    public void onSkipInWrite(ReservationReminderPayload item, Throwable throwable) {
        log.error(
                "Failed to send notification after {} retries - reservationId={}, customerId={}, restaurantName={}, reservationTime={}",
                3,
                item.getReservationId(),
                item.getCustomerId(),
                item.getRestaurantName(),
                item.getReservationTime(),
                throwable);

        FailedNotification failedNotification = new FailedNotification(
                item.getReservationId(),
                item.getCustomerId(),
                item.getCustomerName(),
                item.getRestaurantName(),
                item.getReservationTime(),
                item.getPartySize(),
                throwable.getMessage(),
                LocalDateTime.now(clock)
        );
        failedNotificationRepository.save(failedNotification);
        log.info("Saved failed notification to database: reservationId={}", item.getReservationId());
    }
}
