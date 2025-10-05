package com.wellmeet.batch.job;

import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.kafka.dto.payload.ReservationReminderPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ReservationReminderJobConfig {

    private static final String JOB_NAME = "reservationReminderJob";
    private static final String STEP_NAME = "sendReminderStep";
    private static final int CHUNK_SIZE = 10;
    static final int RETRY_LIMIT = 3;
    private static final int SKIP_LIMIT = 10;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RepositoryItemReader<Reservation> itemReader;
    private final ReservationReminderProcessor processor;
    private final ReservationReminderWriter writer;
    private final NotificationSkipListener skipListener;

    @Bean
    public Job reservationReminderJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(sendReminderStep())
                .build();
    }

    @Bean
    public Step sendReminderStep() {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<Reservation, ReservationReminderPayload>chunk(CHUNK_SIZE, transactionManager)
                .reader(itemReader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(RETRY_LIMIT)
                .skip(Exception.class)
                .skipLimit(SKIP_LIMIT)
                .listener(skipListener)
                .build();
    }
}
