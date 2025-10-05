package com.wellmeet.global.event.listener;

import com.wellmeet.global.event.event.ReservationCanceledEvent;
import com.wellmeet.global.event.event.ReservationCreatedEvent;
import com.wellmeet.global.event.event.ReservationUpdatedEvent;
import com.wellmeet.kafka.dto.payload.ReservationCanceledPayload;
import com.wellmeet.kafka.dto.payload.ReservationCreatedPayload;
import com.wellmeet.kafka.dto.payload.ReservationUpdatedPayload;
import com.wellmeet.kafka.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {

    private final KafkaProducerService kafkaProducerService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReservationCreated(ReservationCreatedEvent event) {
        ReservationCreatedPayload payload = new ReservationCreatedPayload(
                event.getReservationId(),
                event.getMemberName(),
                event.getDateTime(),
                event.getPartySize()
        );
        kafkaProducerService.sendNotificationMessage(event.getRestaurantId(), payload);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReservationUpdated(ReservationUpdatedEvent event) {
        ReservationUpdatedPayload payload = new ReservationUpdatedPayload(
                event.getReservationId(),
                event.getMemberName(),
                event.getDateTime(),
                event.getPartySize()
        );
        kafkaProducerService.sendNotificationMessage(event.getRestaurantId(), payload);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReservationCanceled(ReservationCanceledEvent event) {
        ReservationCanceledPayload payload = new ReservationCanceledPayload(
                event.getReservationId(),
                event.getMemberName(),
                event.getDateTime(),
                event.getPartySize()
        );
        kafkaProducerService.sendNotificationMessage(event.getRestaurantId(), payload);
    }
}
