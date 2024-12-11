package ru.tbank.submissionservice.service.test_case.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;
import ru.tbank.submissionservice.client.YandexCloudS3Client;
import ru.tbank.submissionservice.config.ApplicationConfig;
import ru.tbank.submissionservice.dto.test_case.TestCase;
import ru.tbank.submissionservice.service.test_case.TestCaseService;
import ru.tbank.submissionservice.service.zip.impl.ZipServiceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class TestCaseServiceImplTest {

    private static final String ACCESS_KEY_ID = "access-key-id";
    private static final String SECRET_ACCESS_KEY = "secret-access-key";
    private static final String SUBMISSION_SOURCE_CODE_BUCKET_NAME = "submission-source-code-bucket-name";
    private static final String TEST_ARCHIVE_BUCKET_NAME = "test-archive-bucket-name";
    private static final String TEST_CASES_ARCHIVE_PATH = "classpath:test-case/test-cases.zip";
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
    private TestCaseService service;

    @BeforeEach
    void init() {
        this.client = Mockito.mock(YandexCloudS3Client.class);
        this.service = new TestCaseServiceImpl(
                new ZipServiceImpl(new ObjectMapper()),
                client,
                applicationConfig
        );
    }

    // Upload test archive
    @Test
    void givenProblemIdAndContent_whenUploadTestArchive_thenCallClientUploadFile() throws IOException {
        // Arrange
        int problemId = 1;
        byte[] content = "content".getBytes();
        List<TestCase> testCases = List.of(
                new TestCase("input 1", "output 1"),
                new TestCase("input 2", "output 2")
        );
        String expectedKey = "problem_id/" + problemId + "/test-cases.zip";

        // Act
        service.saveTestCases(problemId, testCases);

        // Assert
        Mockito.verify(client).uploadFile(Mockito.any(byte[].class), Mockito.eq(TEST_ARCHIVE_BUCKET_NAME), Mockito.eq(expectedKey));
    }

    // Get Test Cases
    @Test
    void givenValidTestCases_whenGetTestCases_thenSuccessfullyReturnTestCases() throws IOException {
        // Arrange
        File testCasesZipFile = ResourceUtils.getFile(TEST_CASES_ARCHIVE_PATH);
        int problemId = 1;
        String key = "problem_id/1/test-cases.zip";
        List<TestCase> expectedTestCases = List.of(
                new TestCase("input 1", "output 1"),
                new TestCase("input 2", "output 2")
        );
        Mockito.when(
                client.getFileContent(applicationConfig.yandexCloud().testArchiveBucketName(), key)
        ).thenReturn(
                Files.readAllBytes(testCasesZipFile.toPath())
        );

        // Act
        List<TestCase> testCases = service.getTestCases(problemId);

        // Assert
        Mockito.verify(client)
                .getFileContent(applicationConfig.yandexCloud().testArchiveBucketName(), key);
        Assertions.assertIterableEquals(expectedTestCases, testCases);
    }
}