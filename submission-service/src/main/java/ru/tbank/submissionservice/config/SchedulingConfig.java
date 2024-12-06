package ru.tbank.submissionservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class SchedulingConfig {
    @Bean
    public ScheduledExecutorService scheduledExecutorService(ApplicationConfig applicationConfig) {
        return Executors.newScheduledThreadPool(
                applicationConfig.scheduling().numberOfThreads()
        );
    }
}
