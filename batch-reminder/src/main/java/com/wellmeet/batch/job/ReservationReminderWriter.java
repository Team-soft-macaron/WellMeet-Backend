package com.wellmeet.batch.job;

import com.wellmeet.kafka.dto.payload.ReservationReminderPayload;
import com.wellmeet.kafka.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationReminderWriter implements ItemWriter<ReservationReminderPayload> {

    private final KafkaProducerService kafkaProducerService;

    @Override
    public void write(Chunk<? extends ReservationReminderPayload> chunk) {
        for (ReservationReminderPayload payload : chunk) {
            try {
                String recipient = payload.getCustomerId();
                kafkaProducerService.sendNotificationMessage(recipient, payload);
                log.info("Sent reminder notification for reservation ID: {}", payload.getReservationId());
            } catch (Exception e) {
                log.error("Failed to send reminder notification for reservation ID: {}",
                        payload.getReservationId(), e);
            }
        }
    }
}
