package ru.tbank.submissionservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3Config {
    @Bean
    public S3Client s3Client(ApplicationConfig applicationConfig) {
        AwsCredentials credentials = AwsBasicCredentials.create(
                applicationConfig.yandexCloud().accessKeyId(),
                applicationConfig.yandexCloud().secretAccessKey()
        );

        return S3Client.builder()
                .endpointOverride(URI.create("https://storage.yandexcloud.net"))
                .region(Region.of("ru-central1"))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
