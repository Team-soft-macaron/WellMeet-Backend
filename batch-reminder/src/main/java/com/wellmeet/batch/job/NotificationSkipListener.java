package com.wellmeet.batch.job;

import com.wellmeet.kafka.dto.payload.ReservationReminderPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;

@Slf4j
public class NotificationSkipListener implements SkipListener<ReservationReminderPayload, ReservationReminderPayload> {

    @Override
    public void onSkipInRead(Throwable t) {
        log.error("Skipped item during read", t);
    }

    @Override
    public void onSkipInProcess(ReservationReminderPayload item, Throwable t) {
        log.error("Skipped item during process: reservationId={}", item.getReservationId(), t);
    }

    @Override
    public void onSkipInWrite(ReservationReminderPayload item, Throwable t) {
        log.error("Failed to send notification after {} retries - reservationId={}, customerId={}, restaurantName={}, reservationTime={}",
                3,
                item.getReservationId(),
                item.getCustomerId(),
                item.getRestaurantName(),
                item.getReservationTime(),
                t);
    }
}
