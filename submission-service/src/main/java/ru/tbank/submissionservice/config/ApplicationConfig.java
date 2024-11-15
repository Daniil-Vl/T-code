package ru.tbank.submissionservice.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record ApplicationConfig(
        @NotNull
        YandexCloud yandexCloud
) {
    public record YandexCloud(
            @NotBlank String accessKeyId,
            @NotBlank String secretAccessKey,
            @NotBlank String submissionSourceCodeBucketName,
            @NotBlank String testArchiveBucketName
    ) {
    }
}
