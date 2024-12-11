package ru.tbank.submissionservice.service.test_case.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.submissionservice.client.YandexCloudS3Client;
import ru.tbank.submissionservice.config.ApplicationConfig;
import ru.tbank.submissionservice.dto.test_case.TestCase;
import ru.tbank.submissionservice.service.test_case.TestCaseService;
import ru.tbank.submissionservice.service.zip.ZipService;

import java.io.IOException;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class TestCaseServiceImpl implements TestCaseService {

    private final ZipService zipService;
    private final YandexCloudS3Client client;
    private final ApplicationConfig applicationConfig;

    @Override
    public List<TestCase> getTestCases(long problemId) throws IOException {
        String key = buildTestArchiveKey(problemId);
        log.info("Get test cases with key: {}", key);

        byte[] zipContent = client.getFileContent(
                applicationConfig.yandexCloud().testArchiveBucketName(),
                key
        );

        return zipService.unzipTestCases(zipContent);
    }

    @Override
    public void saveTestCases(long problemId, List<TestCase> testCases) throws IOException {
        byte[] bytes = zipService.zipTestCases(testCases);
        String key = buildTestArchiveKey(problemId);
        log.info("Upload test archive with key: {}", key);

        client.uploadFile(
                bytes,
                applicationConfig.yandexCloud().testArchiveBucketName(),
                key
        );
    }

    private String buildTestArchiveKey(long problemId) {
        return "problem_id/" + problemId + "/test-cases.zip";
    }

}
