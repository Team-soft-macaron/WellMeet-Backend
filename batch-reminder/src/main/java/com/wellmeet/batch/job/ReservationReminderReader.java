package com.wellmeet.batch.job;

import com.wellmeet.batch.client.ReservationClient;
import com.wellmeet.common.dto.ReservationDTO;
import com.wellmeet.common.dto.ReservationStatus;
import java.time.Clock;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ReservationReminderReader {

    private static final int REMINDER_HOURS_BEFORE = 3;
    private static final int TIME_WINDOW_MINUTES = 10;

    private final ReservationClient reservationClient;
    private final Clock clock;

    @Bean
    @StepScope
    public ListItemReader<ReservationDTO> itemReader() {
        log.info("Setting up reader for confirmed reservations");

        List<ReservationDTO> confirmedReservations = reservationClient
                .getReservationsByStatus(ReservationStatus.CONFIRMED);

        log.info("Loaded {} confirmed reservations", confirmedReservations.size());

        return new ListItemReader<>(confirmedReservations);
    }
}
