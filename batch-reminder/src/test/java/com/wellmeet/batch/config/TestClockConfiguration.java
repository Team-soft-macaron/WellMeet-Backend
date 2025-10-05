package com.wellmeet.batch.config;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestClockConfiguration {

    public static final Instant FIXED_INSTANT = Instant.parse("2025-10-05T06:00:00Z");
    public static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    @Bean
    @Primary
    public Clock clock() {
        return Clock.fixed(FIXED_INSTANT, ZONE_ID);
    }
}
