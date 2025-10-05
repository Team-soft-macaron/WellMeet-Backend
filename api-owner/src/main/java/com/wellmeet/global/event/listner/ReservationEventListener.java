package com.wellmeet.global.event.listner;

import com.wellmeet.global.event.event.ReservationConfirmedEvent;
import com.wellmeet.kafka.dto.payload.ReservationConfirmedPayload;
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
    public void handleReservationConfirmed(ReservationConfirmedEvent event) {
        ReservationConfirmedPayload payload = new ReservationConfirmedPayload(
                event.getReservationId(),
                event.getMemberName(),
                event.getDateTime(),
                event.getPartySize()
        );
        kafkaProducerService.sendNotificationMessage(event.getMemberId(), payload);
    }
}
