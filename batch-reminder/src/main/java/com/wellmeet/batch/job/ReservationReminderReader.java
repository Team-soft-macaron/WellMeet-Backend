package com.wellmeet.batch.job;

import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import com.wellmeet.domain.reservation.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationReminderReader implements ItemReader<Reservation> {

    private static final int REMINDER_HOURS_BEFORE = 3;
    private static final int TIME_WINDOW_MINUTES = 10;

    private final ReservationRepository reservationRepository;
    private List<Reservation> reservations;
    private int currentIndex = 0;

    @Override
    public Reservation read() {
        if (reservations == null) {
            reservations = fetchReservations();
            log.info("Fetched {} reservations for reminder", reservations.size());
        }

        if (currentIndex < reservations.size()) {
            return reservations.get(currentIndex++);
        } else {
            reset();
            return null;
        }
    }

    private List<Reservation> fetchReservations() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusHours(REMINDER_HOURS_BEFORE);
        LocalDateTime end = start.plusMinutes(TIME_WINDOW_MINUTES);

        log.info("Searching reservations between {} and {}", start, end);

        return reservationRepository.findReservationsForReminder(
                ReservationStatus.CONFIRMED,
                start.toLocalDate(),
                start.toLocalTime(),
                end.toLocalDate(),
                end.toLocalTime()
        );
    }

    private void reset() {
        reservations = null;
        currentIndex = 0;
    }
}
