package com.wellmeet.batch.job;

import com.wellmeet.domain.member.MemberDomainService;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.restaurant.RestaurantDomainService;
import java.time.Clock;
import java.time.LocalDateTime;
import com.wellmeet.kafka.dto.payload.ReservationReminderPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationReminderProcessor implements ItemProcessor<Reservation, ReservationReminderPayload> {

    private static final int REMINDER_HOURS_BEFORE = 3;
    private static final int TIME_WINDOW_MINUTES = 10;

    private final MemberDomainService memberDomainService;
    private final RestaurantDomainService restaurantDomainService;
    private final Clock clock;

    @Override
    public ReservationReminderPayload process(Reservation reservation) {
        log.info("Processing reservation reminder for reservation ID: {}", reservation.getId());

        var availableDate = restaurantDomainService.getAvailableDate(
                reservation.getAvailableDateId(), reservation.getRestaurantId());
        LocalDateTime dateTime = LocalDateTime.of(availableDate.getDate(), availableDate.getTime());

        // Filter by time window
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime reminderStart = now.plusHours(REMINDER_HOURS_BEFORE);
        LocalDateTime reminderEnd = reminderStart.plusMinutes(TIME_WINDOW_MINUTES);

        if (dateTime.isBefore(reminderStart) || dateTime.isAfter(reminderEnd)) {
            log.debug("Reservation {} outside time window, skipping", reservation.getId());
            return null;  // Spring Batch filters out null returns
        }

        Member member = memberDomainService.getById(reservation.getMemberId());
        var restaurant = restaurantDomainService.getById(reservation.getRestaurantId());

        return new ReservationReminderPayload(
                reservation.getId(),
                reservation.getMemberId(),
                member.getName(),
                restaurant.getName(),
                dateTime,
                reservation.getPartySize()
        );
    }
}
