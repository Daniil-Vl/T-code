package ru.tbank.submissionservice.client;

import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;

public interface YandexCloudS3Client {

    /**
     * Uploads file to yandex cloud object storage
     *
     * @param fileContent - file`s content in text format
     * @param bucket
     * @param key
     * @return
     */
    PutObjectResponse uploadFile(String fileContent, String bucket, String key);

    /**
     * Uploads file to yandex cloud object storage
     *
     * @param fileContentBytes - file`s content in binary format
     * @param bucket
     * @param key
     * @return
     */
    PutObjectResponse uploadFile(byte[] fileContentBytes, String bucket, String key);

    /**
     * Download file and return it`s content bytes
     *
     * @param bucket
     * @param key
     * @return
     * @throws IOException
     */
    byte[] getFileContent(String bucket, String key) throws IOException;

}
