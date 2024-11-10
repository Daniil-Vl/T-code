package ru.tbank.submissionservice.client;

import reactor.core.publisher.Mono;
import ru.tbank.submissionservice.dto.SubmissionRequestBody;
import ru.tbank.submissionservice.dto.SubmissionResult;
import ru.tbank.submissionservice.dto.SubmissionToken;
import ru.tbank.submissionservice.enums.Language;

import java.util.List;

public interface Judge0Client {
    SubmissionToken submit(String sourceCode, Language language, String stdin);

    Mono<SubmissionResult> submitWaiting(String sourceCode, Language language, String stdin);

    List<SubmissionToken> submitBatch(List<SubmissionRequestBody> submissionRequests);

    SubmissionResult getSubmissionResult(String submissionToken);
}
