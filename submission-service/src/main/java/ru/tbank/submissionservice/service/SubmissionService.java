package ru.tbank.submissionservice.service;

import reactor.core.publisher.Mono;
import ru.tbank.submissionservice.dto.SubmissionRequestBody;
import ru.tbank.submissionservice.dto.SubmissionResult;
import ru.tbank.submissionservice.dto.SubmissionToken;

import java.util.List;

public interface SubmissionService {
    SubmissionToken submit(String sourceCode, String language, String stdin);

    Mono<SubmissionResult> submitWaiting(String sourceCode, String language, String stdin);

    List<SubmissionToken> submitBatch(List<SubmissionRequestBody> requests);

    List<SubmissionResult> getBatchSubmissionResult(List<SubmissionToken> tokens);
}
