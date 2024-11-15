package ru.tbank.submissionservice.client;

import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;

public interface YandexCloudS3Client {

    PutObjectResponse uploadFile(String fileContent, String bucket, String key);

    PutObjectResponse uploadFile(byte[] fileContentBytes, String bucket, String key);

    byte[] getFileContent(String bucket, String key) throws IOException;
}
