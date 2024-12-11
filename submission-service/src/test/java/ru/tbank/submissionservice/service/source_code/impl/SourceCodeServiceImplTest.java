package ru.tbank.submissionservice.service.source_code.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.submissionservice.client.YandexCloudS3Client;
import ru.tbank.submissionservice.config.ApplicationConfig;
import ru.tbank.submissionservice.model.entities.id.SubmissionId;
import ru.tbank.submissionservice.service.source_code.SourceCodeService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@ExtendWith(MockitoExtension.class)
class SourceCodeServiceImplTest {

    private static final String ACCESS_KEY_ID = "access-key-id";
    private static final String SECRET_ACCESS_KEY = "secret-access-key";
    private static final String SUBMISSION_SOURCE_CODE_BUCKET_NAME = "submission-source-code-bucket-name";
    private static final String TEST_ARCHIVE_BUCKET_NAME = "test-archive-bucket-name";
    private static final String S3_ENDPOINT = "endpoint";
    private static final String S3_REGION = "region";

    private final ApplicationConfig applicationConfig = new ApplicationConfig(
            null,
            null,
            null,
            new ApplicationConfig.YandexCloud(
                    ACCESS_KEY_ID,
                    SECRET_ACCESS_KEY,
                    SUBMISSION_SOURCE_CODE_BUCKET_NAME,
                    TEST_ARCHIVE_BUCKET_NAME,
                    S3_ENDPOINT,
                    S3_REGION
            ),
            null
    );
    private YandexCloudS3Client client;
    private SourceCodeService service;

    @BeforeEach
    void init() {
        this.client = Mockito.mock(YandexCloudS3Client.class);
        this.service = new SourceCodeServiceImpl(
                client,
                applicationConfig
        );
    }

    @Test
    void givenUserIdProblemIdSourceCode_whenUploadSourceCode_thenReturnKeyAndUploadFile() {
        // Arrange
        int contestId = 1;
        int userId = 2;
        int problemId = 3;
        SubmissionId submissionId = new SubmissionId(userId, contestId, problemId);
        String sourceCode = "source-code-content";
        String expectedKey = "contest_id/1/user_id/2/problem_id/3/source-code";

        // Act
        String actualKey = service.uploadSourceCode(submissionId, sourceCode);

        // Assert
        Mockito.verify(client).uploadFile(sourceCode, applicationConfig.yandexCloud().submissionSourceCodeBucketName(), expectedKey);
        Assertions.assertEquals(expectedKey, actualKey);
    }

    @Test
    void givenUserIdProblemId_whenDownloadSourceCode_thenSuccessfullyDownload() throws IOException {
        // Arrange
        int contestId = 1;
        int userId = 2;
        int problemId = 3;
        SubmissionId submissionId = new SubmissionId(userId, contestId, problemId);
        String expectedSourceCode = "source-code-content";
        String key = "contest_id/1/user_id/2/problem_id/3/source-code";
        Mockito.when(
                client.getFileContent(applicationConfig.yandexCloud().submissionSourceCodeBucketName(), key)
        ).thenReturn(
                expectedSourceCode.getBytes(StandardCharsets.UTF_8)
        );

        // Act
        String actualSourceCode = service.downloadSourceCode(submissionId);

        // Assert
        Mockito.verify(client).getFileContent(applicationConfig.yandexCloud().submissionSourceCodeBucketName(), key);
        Assertions.assertEquals(expectedSourceCode, actualSourceCode);
    }

}