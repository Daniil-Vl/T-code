package ru.tbank.submissionservice.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.submissionservice.client.YandexCloudS3Client;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class YandexCloudS3ClientImpl implements YandexCloudS3Client {

    private final S3Client s3Client;

    @Override
    public PutObjectResponse uploadFile(String fileContent, String bucket, String key) {
        logUploadingFile(bucket, key);

        return s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build(),
                RequestBody.fromString(fileContent)
        );
    }

    @Override
    public PutObjectResponse uploadFile(byte[] fileContentBytes, String bucket, String key) {
        logUploadingFile(bucket, key);

        return s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build(),
                RequestBody.fromBytes(fileContentBytes)
        );
    }

    @Override
    public byte[] getFileContent(String bucket, String key) throws IOException {
        log.info("Getting file {} from bucket {}", key, bucket);

        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build()
        );

        return response.readAllBytes();
    }

    private void logUploadingFile(String bucket, String key) {
        log.info("Uploading file to bucket {} with key {}", bucket, key);
    }
}
