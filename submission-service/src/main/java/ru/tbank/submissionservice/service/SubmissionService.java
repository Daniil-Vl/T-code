package ru.tbank.submissionservice.service;

import reactor.core.publisher.Mono;
import ru.tbank.submissionservice.dto.SubmissionRequestBody;
import ru.tbank.submissionservice.dto.SubmissionResult;
import ru.tbank.submissionservice.dto.SubmissionToken;
import ru.tbank.submissionservice.enums.Language;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SubmissionService {
    SubmissionToken submit(String sourceCode, Language language, String stdin);

    Mono<SubmissionResult> submitWaiting(String sourceCode, Language language, String stdin);

    List<SubmissionToken> submitBatch(List<SubmissionRequestBody> requests);

    CompletableFuture<SubmissionResult> getSubmissionResult(String submissionToken) throws InterruptedException;
}
