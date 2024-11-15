package ru.tbank.submissionservice.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;
import ru.tbank.submissionservice.client.YandexCloudS3Client;
import ru.tbank.submissionservice.config.ApplicationConfig;
import ru.tbank.submissionservice.dto.TestCase;
import ru.tbank.submissionservice.exception.InvalidTestCases;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class YandexCloudS3ServiceImplTest {

    private static final String ACCESS_KEY_ID = "access-key-id";
    private static final String SECRET_ACCESS_KEY = "secret-access-key";
    private static final String SUBMISSION_SOURCE_CODE_BUCKET_NAME = "submission-source-code-bucket-name";
    private static final String TEST_ARCHIVE_BUCKET_NAME = "test-archive-bucket-name";

    private static final String TEST_CASES_ARCHIVE_PATH = "classpath:test-case/test-cases.zip";
    private static final String NO_OUTPUT_TEST_CASES_ARCHIVE_PATH = "classpath:test-case/no-output-test-cases.zip";
    private static final String NO_NUMBER_TEST_CASES_ARCHIVE_PATH = "classpath:test-case/no-number-test-cases.zip";

    @Mock
    private YandexCloudS3Client client;

    @Spy
    private ApplicationConfig applicationConfig = new ApplicationConfig(
            new ApplicationConfig.YandexCloud(
                    ACCESS_KEY_ID,
                    SECRET_ACCESS_KEY,
                    SUBMISSION_SOURCE_CODE_BUCKET_NAME,
                    TEST_ARCHIVE_BUCKET_NAME
            )
    );

    @InjectMocks
    private YandexCloudS3ServiceImpl service;

    // Upload test archive
    @Test
    void givenProblemIdAndContent_whenUploadTestArchive_thenCallClientUploadFile() {
        // Arrange
        int problemId = 1;
        byte[] content = "content".getBytes();
        String expectedKey = "1/test-cases.zip";

        // Act
        String actualKey = service.uploadTestArchive(problemId, content);

        // Assert
        Mockito.verify(client).uploadFile(content, TEST_ARCHIVE_BUCKET_NAME, expectedKey);
        Assertions.assertEquals(expectedKey, actualKey);
    }

    // Get Test Cases
    @Test
    void givenValidTestCases_whenGetTestCases_thenSuccessfullyReturnTestCases() throws IOException {
        // Arrange
        File testCasesZipFile = ResourceUtils.getFile(TEST_CASES_ARCHIVE_PATH);
        int problemId = 1;
        String key = "1/test-cases.zip";
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

    @Test
    void givenFileWithoutNumberInArchive_whenGetTestCases_thenThrowInvalidTestCaseException() throws IOException {
        // Arrange
        File testCasesZipFile = ResourceUtils.getFile(NO_NUMBER_TEST_CASES_ARCHIVE_PATH);
        int problemId = 1;
        String key = "1/test-cases.zip";
        Mockito.when(
                client.getFileContent(applicationConfig.yandexCloud().testArchiveBucketName(), key)
        ).thenReturn(
                Files.readAllBytes(testCasesZipFile.toPath())
        );

        // Act + Assert
        Assertions.assertThrows(InvalidTestCases.class, () -> service.getTestCases(problemId));
        Mockito.verify(client)
                .getFileContent(applicationConfig.yandexCloud().testArchiveBucketName(), key);
    }

    @Test
    void givenTestCaseWithoutOutputInArchive_whenGetTestCases_thenThrowInvalidTestCaseException() throws IOException {
        // Arrange
        File testCasesZipFile = ResourceUtils.getFile(NO_OUTPUT_TEST_CASES_ARCHIVE_PATH);
        int problemId = 1;
        String key = "1/test-cases.zip";
        Mockito.when(
                client.getFileContent(applicationConfig.yandexCloud().testArchiveBucketName(), key)
        ).thenReturn(
                Files.readAllBytes(testCasesZipFile.toPath())
        );

        // Act + Assert
        Assertions.assertThrows(InvalidTestCases.class, () -> service.getTestCases(problemId));
        Mockito.verify(client)
                .getFileContent(applicationConfig.yandexCloud().testArchiveBucketName(), key);
    }

    @Test
    void givenUserIdProblemIdSourceCode_whenUploadSourceCode_thenReturnKeyAndUploadFile() {
        // Arrange
        int userId = 1;
        int problemId = 1;
        String sourceCode = "source-code-content";
        String expectedKey = "1/1/source-code";

        // Act
        String actualKey = service.uploadSourceCode(userId, problemId, sourceCode);

        // Assert
        Mockito.verify(client).uploadFile(sourceCode, applicationConfig.yandexCloud().submissionSourceCodeBucketName(), expectedKey);
        Assertions.assertEquals(expectedKey, actualKey);
    }

    @Test
    void givenUserIdProblemId_whenDownloadSourceCode_thenSuccessfullyDownload() throws IOException {
        // Arrange
        int userId = 1;
        int problemId = 1;
        String expectedSourceCode = "source-code-content";
        String key = "1/1/source-code";
        Mockito.when(
                client.getFileContent(applicationConfig.yandexCloud().submissionSourceCodeBucketName(), key)
        ).thenReturn(
                expectedSourceCode.getBytes(StandardCharsets.UTF_8)
        );

        // Act
        String actualSourceCode = service.downloadSourceCode(userId, problemId);

        // Assert
        Mockito.verify(client).getFileContent(applicationConfig.yandexCloud().submissionSourceCodeBucketName(), key);
        Assertions.assertEquals(expectedSourceCode, actualSourceCode);
    }

}