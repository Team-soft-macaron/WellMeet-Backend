package com.wellmeet.global.event.listener;

import com.wellmeet.global.event.event.ReservationCreatedEvent;
import com.wellmeet.kafka.dto.ReservationCreatedPayload;
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
                event.getMemberId(),
                event.getDateTime(),
                event.getPartySize()
        );
        kafkaProducerService.sendNotificationMessage(event.getRestaurantId(), payload);
    }
}
