package ru.tbank.submissionservice.service.source_code.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.submissionservice.client.YandexCloudS3Client;
import ru.tbank.submissionservice.config.ApplicationConfig;
import ru.tbank.submissionservice.model.entities.id.SubmissionId;
import ru.tbank.submissionservice.service.source_code.SourceCodeService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class SourceCodeServiceImpl implements SourceCodeService {

    private final YandexCloudS3Client client;
    private final ApplicationConfig applicationConfig;

    @Override
    public String uploadSourceCode(SubmissionId submissionId, String sourceCode) {
        String key = buildSourceCodeKey(submissionId);
        log.info("Upload source code with key: {}", key);

        client.uploadFile(
                sourceCode,
                applicationConfig.yandexCloud().submissionSourceCodeBucketName(),
                key
        );

        return key;
    }

    @Override
    public String downloadSourceCode(SubmissionId submissionId) throws IOException {
        String key = buildSourceCodeKey(submissionId);
        log.info("Download source code with key: {}", key);

        byte[] fileContent = client.getFileContent(
                applicationConfig.yandexCloud().submissionSourceCodeBucketName(),
                key
        );

        return new String(fileContent, StandardCharsets.UTF_8);
    }

    private String buildSourceCodeKey(SubmissionId submissionId) {
        return "contest_id/" + submissionId.getContestId()
                + "/user_id/" + submissionId.getUserId()
                + "/problem_id/" + submissionId.getProblemId()
                + "/source-code";
    }
}
