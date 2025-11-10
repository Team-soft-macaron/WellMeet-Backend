package com.wellmeet.batch.job;

import com.wellmeet.batch.client.AvailableDateClient;
import com.wellmeet.batch.client.MemberClient;
import com.wellmeet.batch.client.RestaurantClient;
import com.wellmeet.common.dto.AvailableDateDTO;
import com.wellmeet.common.dto.MemberDTO;
import com.wellmeet.common.dto.ReservationDTO;
import com.wellmeet.common.dto.RestaurantDTO;
import com.wellmeet.kafka.dto.payload.ReservationReminderPayload;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationReminderProcessor implements ItemProcessor<ReservationDTO, ReservationReminderPayload> {

    private static final int REMINDER_HOURS_BEFORE = 3;
    private static final int TIME_WINDOW_MINUTES = 10;

    private final MemberClient memberClient;
    private final RestaurantClient restaurantClient;
    private final AvailableDateClient availableDateClient;
    private final Clock clock;

    @Override
    public ReservationReminderPayload process(ReservationDTO reservation) {
        log.info("Processing reservation reminder for reservation ID: {}", reservation.id());

        List<AvailableDateDTO> availableDates = availableDateClient.getAvailableDatesByIds(
                new AvailableDateClient.AvailableDateIdsRequest(List.of(reservation.availableDateId()))
        );

        if (availableDates.isEmpty()) {
            log.warn("AvailableDate not found for reservation {}", reservation.id());
            return null;
        }

        AvailableDateDTO availableDate = availableDates.get(0);
        LocalDateTime dateTime = LocalDateTime.of(availableDate.date(), availableDate.time());

        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime reminderStart = now.plusHours(REMINDER_HOURS_BEFORE);
        LocalDateTime reminderEnd = reminderStart.plusMinutes(TIME_WINDOW_MINUTES);

        if (dateTime.isBefore(reminderStart) || dateTime.isAfter(reminderEnd)) {
            log.debug("Reservation {} outside time window, skipping", reservation.id());
            return null;
        }

        MemberDTO member = memberClient.getMemberById(reservation.memberId());
        RestaurantDTO restaurant = restaurantClient.getRestaurantById(reservation.restaurantId());

        return new ReservationReminderPayload(
                reservation.id(),
                reservation.memberId(),
                member.name(),
                restaurant.name(),
                dateTime,
                reservation.partySize()
        );
    }
}
