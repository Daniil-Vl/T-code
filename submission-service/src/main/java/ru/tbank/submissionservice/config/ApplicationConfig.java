package ru.tbank.submissionservice.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Duration;


@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
        @NotBlank
        String judge0ApiBaseUrl,

        @NotNull
        Retry retry,

        @NotNull
        Scheduling scheduling,
        
        @NotNull
        YandexCloud yandexCloud
) {
    public record Retry(
            @NotNull
            @PositiveOrZero
            int retryMaxAttempts,

            @NotNull
            Duration retryMinTime,

            @DecimalMin(value = "0.0")
            @DecimalMax(value = "1.0")
            BigDecimal jitterFactor
    ) {
    }

    public record Scheduling(
            @Positive
            int numberOfThreads
    ) {
    }
    
    public record YandexCloud(
            @NotBlank String accessKeyId,
            @NotBlank String secretAccessKey,
            @NotBlank String submissionSourceCodeBucketName,
            @NotBlank String testArchiveBucketName
    ) {
    }
}
