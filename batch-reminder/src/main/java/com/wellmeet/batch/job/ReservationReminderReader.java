package com.wellmeet.batch.job;

import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import com.wellmeet.domain.reservation.repository.ReservationRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ReservationReminderReader {

    private static final int REMINDER_HOURS_BEFORE = 3;
    private static final int TIME_WINDOW_MINUTES = 10;
    private static final int PAGE_SIZE = 10;

    private final ReservationRepository reservationRepository;
    private final Clock clock;

    @Bean
    @StepScope
    public RepositoryItemReader<Reservation> itemReader() {
        log.info("Setting up reader for confirmed reservations");

        Map<String, Sort.Direction> sorts = Map.of(
                "availableDateId", Sort.Direction.ASC
        );

        return new RepositoryItemReaderBuilder<Reservation>()
                .name("reservationReminderReader")
                .repository(reservationRepository)
                .methodName("findAllByStatusOrderByAvailableDateIdAsc")
                .arguments(List.of(ReservationStatus.CONFIRMED))
                .pageSize(PAGE_SIZE)
                .sorts(sorts)
                .build();
    }
}
