package ru.tbank.contestservice.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
        @NotBlank
        String submissionServiceUrl,

        @NotNull
        Cache cache
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
}
