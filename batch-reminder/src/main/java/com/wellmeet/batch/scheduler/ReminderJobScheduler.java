package com.wellmeet.batch.scheduler;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job reservationReminderJob;

    @Scheduled(cron = "0 */10 * * * *")
    public void runReminderJob() {
        try {
            log.info("Starting reservation reminder job at {}", LocalDateTime.now());

            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(reservationReminderJob, jobParameters);

            log.info("Completed reservation reminder job at {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("Failed to execute reservation reminder job", e);
        }
    }
}
