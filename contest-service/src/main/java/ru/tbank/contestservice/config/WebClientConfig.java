package ru.tbank.contestservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient(ApplicationConfig config) {
        return WebClient
                .builder()
                .baseUrl(config.submissionServiceUrl())
                .build();
    }
}
