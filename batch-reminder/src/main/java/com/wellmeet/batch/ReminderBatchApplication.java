package com.wellmeet.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.wellmeet.batch", "com.wellmeet.domain", "com.wellmeet.kafka"})
@EntityScan(basePackages = {"com.wellmeet.domain", "com.wellmeet.batch.entity"})
@EnableJpaRepositories(basePackages = {"com.wellmeet.domain", "com.wellmeet.batch.repository"})
public class ReminderBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReminderBatchApplication.class, args);
    }
}
