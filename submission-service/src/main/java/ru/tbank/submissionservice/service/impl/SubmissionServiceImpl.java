package ru.tbank.submissionservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.tbank.submissionservice.client.Judge0Client;
import ru.tbank.submissionservice.dto.SubmissionRequestBody;
import ru.tbank.submissionservice.dto.SubmissionResult;
import ru.tbank.submissionservice.dto.SubmissionToken;
import ru.tbank.submissionservice.enums.Language;
import ru.tbank.submissionservice.service.SubmissionService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionServiceImpl implements SubmissionService {

    private static final String IN_QUEUE_STATUS_MESSAGE = "In Queue";
    private static final String PROCESSING_STATUS_MESSAGE = "Processing";

    private final Judge0Client judge0Client;

    @Override
    public SubmissionToken submit(String sourceCode, Language language, String stdin) {
        return judge0Client.submit(sourceCode, language, stdin);
    }

    @Override
    public Mono<SubmissionResult> submitWaiting(String sourceCode, Language language, String stdin) {
        return judge0Client.submitWaiting(sourceCode, language, stdin);
    }

    @Override
    public List<SubmissionToken> submitBatch(List<SubmissionRequestBody> requests) {
        return judge0Client.submitBatch(requests);
    }

    @Async
    @Override
    public CompletableFuture<SubmissionResult> getSubmissionResult(String submissionToken) throws InterruptedException {
        SubmissionResult submissionResult = judge0Client.getSubmissionResult(submissionToken);

        // TODO: Fix busy waiting
        log.info("Start waiting for submission processing...");
        while (!isSubmissionReady(submissionResult)) {
            Thread.sleep(10);
            submissionResult = judge0Client.getSubmissionResult(submissionToken);
        }
        log.info("Submission status and result retrieved");

        return CompletableFuture.completedFuture(submissionResult);
    }

    private boolean isSubmissionReady(SubmissionResult submissionResult) {
        String description = submissionResult.status().description();
        return !description.equals(IN_QUEUE_STATUS_MESSAGE)
                && !description.equals(PROCESSING_STATUS_MESSAGE);
    }

}