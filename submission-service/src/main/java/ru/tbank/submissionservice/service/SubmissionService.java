package ru.tbank.submissionservice.service;

import reactor.core.publisher.Mono;
import ru.tbank.submissionservice.dto.SubmissionRequestBody;
import ru.tbank.submissionservice.dto.SubmissionResult;
import ru.tbank.submissionservice.dto.SubmissionToken;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SubmissionService {
    SubmissionToken submit(String sourceCode, String language, String stdin);

    Mono<SubmissionResult> submitWaiting(String sourceCode, String language, String stdin);

    List<SubmissionToken> submitBatch(List<SubmissionRequestBody> requests);

    CompletableFuture<SubmissionResult> getSubmissionResult(String submissionToken) throws InterruptedException;
}
