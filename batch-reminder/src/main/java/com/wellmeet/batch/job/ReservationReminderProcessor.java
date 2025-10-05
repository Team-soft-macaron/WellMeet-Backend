package com.wellmeet.batch.job;

import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.kafka.dto.payload.ReservationReminderPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReservationReminderProcessor implements ItemProcessor<Reservation, ReservationReminderPayload> {

    @Override
    public ReservationReminderPayload process(Reservation reservation) {
        log.info("Processing reservation reminder for reservation ID: {}", reservation.getId());

        return new ReservationReminderPayload(
                reservation.getId(),
                reservation.getMember().getId(),
                reservation.getMember().getName(),
                reservation.getRestaurantName(),
                reservation.getDateTime(),
                reservation.getPartySize()
        );
    }
}
