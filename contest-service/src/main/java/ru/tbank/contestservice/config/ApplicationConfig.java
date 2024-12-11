package ru.tbank.contestservice.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
        @NotBlank
        String submissionServiceUrl,

        @NotNull
        Cache cache,

        @NotNull
        RabbitMQ rabbitMq
) {
    public record Cache(
            @NotNull
            Language language
    ) {
        public record Language(
                @NotBlank
                String cacheName,
                @NotNull
                Duration expireAfterWrite
        ) {
        }
    }

    public record RabbitMQ(
            @NotBlank String submissionQueueName,
            @NotBlank String submissionDeadLetterQueueName,
            @DefaultValue(value = "false") boolean isDurable
    ) {

    }
}
