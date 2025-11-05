package com.wellmeet.config;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
                5000, TimeUnit.MILLISECONDS,  // connectTimeout
                5000, TimeUnit.MILLISECONDS,  // readTimeout
                true                          // followRedirects
        );
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(
                100,   // period (초기 대기 시간)
                1000,  // maxPeriod (최대 대기 시간)
                3      // maxAttempts (최대 재시도 횟수)
        );
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}
