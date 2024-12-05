package ru.tbank.submissionservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import ru.tbank.submissionservice.exception.ServiceException;

@Configuration
public class ClientConfig {

    @Bean
    public WebClient webClient(ApplicationConfig applicationConfig) {
        return WebClient.builder()
                .baseUrl(applicationConfig.judge0ApiBaseUrl())
                .defaultStatusHandler(
                        HttpStatusCode::is5xxServerError,
                        response -> Mono.error(
                                new ServiceException("External service exception", response.statusCode().value())
                        )
                )
                .build();
    }

    @Bean
    public Retry retry(ApplicationConfig applicationConfig) {
        return Retry
                .backoff(
                        applicationConfig.retry().retryMaxAttempts(),
                        applicationConfig.retry().retryMinTime()
                )
                .jitter(
                        applicationConfig.retry().jitterFactor().doubleValue()
                )
                .filter(throwable -> throwable instanceof ServiceException)
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> {
                    throw new ServiceException(
                            "Failed to call external service after max retries",
                            HttpStatus.SERVICE_UNAVAILABLE.value()
                    );
                }));
    }

}
