package com.wellmeet.batch.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

@ExtendWith(MockitoExtension.class)
class ReminderJobSchedulerTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job reservationReminderJob;

    @InjectMocks
    private ReminderJobScheduler scheduler;

    @Nested
    class RunReminderJob {

        @Test
        void 스케줄러가_Job을_실행한다() throws Exception {
            JobExecution jobExecution = new JobExecution(1L);
            when(jobLauncher.run(eq(reservationReminderJob), any(JobParameters.class)))
                    .thenReturn(jobExecution);

            scheduler.runReminderJob();

            verify(jobLauncher, times(1)).run(eq(reservationReminderJob), any(JobParameters.class));
        }

        @Test
        void Job_실행_실패시_예외를_로깅하고_계속_진행한다() throws Exception {
            when(jobLauncher.run(eq(reservationReminderJob), any(JobParameters.class)))
                    .thenThrow(new RuntimeException("Job execution failed"));

            scheduler.runReminderJob();

            verify(jobLauncher, times(1)).run(eq(reservationReminderJob), any(JobParameters.class));
        }

        @Test
        void 매_호출마다_새로운_JobParameters를_생성한다() throws Exception {
            JobExecution jobExecution1 = new JobExecution(1L);
            JobExecution jobExecution2 = new JobExecution(2L);

            when(jobLauncher.run(eq(reservationReminderJob), any(JobParameters.class)))
                    .thenReturn(jobExecution1)
                    .thenReturn(jobExecution2);

            scheduler.runReminderJob();
            scheduler.runReminderJob();

            verify(jobLauncher, times(2)).run(eq(reservationReminderJob), any(JobParameters.class));
        }
    }
}
