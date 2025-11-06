package com.wellmeet.saga.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ConditionalOnProperty(prefix = "saga", name = "enabled", havingValue = "true", matchIfMissing = false)
@ComponentScan(basePackages = "com.wellmeet.saga")
public class SagaAutoConfiguration {

}
