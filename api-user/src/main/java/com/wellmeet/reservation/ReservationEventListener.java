package com.wellmeet.reservation;

import com.wellmeet.kafka.dto.NotificationMessage;
import com.wellmeet.kafka.dto.ReservationCreatedPayload;
import com.wellmeet.kafka.factory.NotificationMessageFactory;
import com.wellmeet.kafka.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventListener {

    private final KafkaProducerService kafkaProducerService;
    private final NotificationMessageFactory messageFactory;

    @Value("${kafka.topic.notification:notification}")
    private String notificationTopic;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleReservationCreated(ReservationCreatedEvent event) {
        try {
            ReservationCreatedPayload payload = new ReservationCreatedPayload(
                    event.getReservationId(),
                    event.getRestaurantName(),
                    event.getMemberId(), // customerName 대신 memberId 사용
                    event.getDateTime(),
                    event.getPartySize()
            );

            NotificationMessage message = messageFactory.createReservationCreatedMessage(payload);
            kafkaProducerService.sendMessage(notificationTopic, event.getMemberId(), message);

            log.info("Successfully sent reservation created notification to Kafka for reservation: {}",
                    event.getReservationId());
        } catch (Exception e) {
            log.error("Failed to send reservation created notification to Kafka for reservation: {}",
                    event.getReservationId(), e);
        }
    }
}
