package ru.tbank.submissionservice.service;

import ru.tbank.submissionservice.model.entities.SubmissionEntity;
import ru.tbank.submissionservice.model.entities.id.SubmissionId;
import java.time.OffsetDateTime;
import reactor.core.publisher.Mono;
import ru.tbank.submissionservice.dto.SubmissionRequestBody;
import ru.tbank.submissionservice.dto.SubmissionResult;
import ru.tbank.submissionservice.dto.SubmissionToken;

import java.util.List;

public interface SubmissionService {
    SubmissionEntity createSubmission(long userId, long contestId, long problemId, String codeKey, OffsetDateTime submittedAt);

    void updateSubmission(SubmissionId submissionId, String status, int score, int executionTimeMs, int memoryUserKb);
  
    SubmissionToken submit(String sourceCode, String language, String stdin);

    Mono<SubmissionResult> submitWaiting(String sourceCode, String language, String stdin);

    List<SubmissionToken> submitBatch(List<SubmissionRequestBody> requests);

    List<SubmissionResult> getBatchSubmissionResult(List<SubmissionToken> tokens);
}
