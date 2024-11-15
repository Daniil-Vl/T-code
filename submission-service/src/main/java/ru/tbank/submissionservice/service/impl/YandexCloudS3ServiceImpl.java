package ru.tbank.submissionservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.submissionservice.client.YandexCloudS3Client;
import ru.tbank.submissionservice.config.ApplicationConfig;
import ru.tbank.submissionservice.dto.TestCase;
import ru.tbank.submissionservice.exception.InvalidTestCases;
import ru.tbank.submissionservice.service.YandexCloudS3Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class YandexCloudS3ServiceImpl implements YandexCloudS3Service {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

    private final YandexCloudS3Client client;
    private final ApplicationConfig applicationConfig;

    @Override
    public String uploadTestArchive(int problemId, byte[] testArchiveContent) {
        String key = buildTestArchiveKey(problemId);
        log.info("Upload test archive with key: {}", key);

        client.uploadFile(
                testArchiveContent,
                applicationConfig.yandexCloud().testArchiveBucketName(),
                key
        );

        return key;
    }

    @Override
    public List<TestCase> getTestCases(int problemId) throws IOException {
        String key = buildTestArchiveKey(problemId);
        log.info("Get test cases with key: {}", key);

        byte[] zipContent = client.getFileContent(
                applicationConfig.yandexCloud().testArchiveBucketName(),
                key
        );

        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipContent));
        ZipEntry zipEntry = zis.getNextEntry();
        Map<Integer, TestCase> testCases = new HashMap<>();

        // Read all files from zip and gather file pair (input_n.txt, output_n.txt)
        while (zipEntry != null) {
            byte[] bytes = zis.readAllBytes();

            String filename = zipEntry.getName();
            String strContent = new String(bytes, StandardCharsets.UTF_8);

            Matcher numberMatcher = NUMBER_PATTERN.matcher(strContent);

            if (!numberMatcher.find()) {
                log.error("File hasn't number: {}", filename);
                throw new InvalidTestCases("File hasn't number: " + filename);
            }

            int fileNumber = Integer.parseInt(numberMatcher.group());
            TestCase testCase = testCases.getOrDefault(fileNumber, new TestCase());

            if (filename.startsWith("input")) {
                testCase.setInput(strContent);
            } else {
                testCase.setOutput(strContent);
            }

            testCases.put(fileNumber, testCase);
            zipEntry = zis.getNextEntry();
        }

        ArrayList<TestCase> resultTestCases = new ArrayList<>(testCases.values());
        validateTestCases(resultTestCases);
        return resultTestCases;
    }

    @Override
    public String uploadSourceCode(int userId, int problemId, String sourceCode) {
        String key = buildSourceCodeKey(userId, problemId);
        log.info("Upload source code with key: {}", key);

        client.uploadFile(
                sourceCode,
                applicationConfig.yandexCloud().submissionSourceCodeBucketName(),
                key
        );

        return key;
    }

    @Override
    public String downloadSourceCode(int userId, int problemId) throws IOException {
        String key = buildSourceCodeKey(userId, problemId);
        log.info("Download source code with key: {}", key);

        byte[] fileContent = client.getFileContent(
                applicationConfig.yandexCloud().submissionSourceCodeBucketName(),
                key
        );

        return new String(fileContent, StandardCharsets.UTF_8);
    }

    private void validateTestCases(List<TestCase> testCases) {
        for (TestCase testCase : testCases) {
            if (testCase.getInput() == null || testCase.getOutput() == null) {
                log.error("Some test case hasn't input or output: {}", testCase);
                throw new InvalidTestCases("Test case input or output wasn't found in archive");
            }
        }
    }

    private String buildTestArchiveKey(int problemId) {
        return problemId + "/test-cases.zip";
    }

    private String buildSourceCodeKey(int userId, int problemId) {
        return userId + "/" + problemId + "/source-code";
    }

}
