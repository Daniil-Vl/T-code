package ru.tbank.submissionservice.service.rabbit.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.tbank.submissionservice.dto.submission.SubmissionMessage;
import ru.tbank.submissionservice.dto.submission.SubmissionRequestBody;
import ru.tbank.submissionservice.dto.submission.SubmissionToken;
import ru.tbank.submissionservice.dto.test_case.TestCase;
import ru.tbank.submissionservice.service.rabbit.RabbitMQService;
import ru.tbank.submissionservice.service.submission.SubmissionResultFetcher;
import ru.tbank.submissionservice.service.submission.SubmissionService;
import ru.tbank.submissionservice.service.test_case.TestCaseService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RabbitMQServiceImpl implements RabbitMQService {

    private final SubmissionResultFetcher fetcher;
    private final SubmissionService submissionService;
    private final TestCaseService testCaseService;

    @Override
    @RabbitListener(queues = "${app.rabbit-mq.submission-queue-name}")
    public void processSubmissionMessage(@Payload @Valid SubmissionMessage message) throws IOException {
        log.info("Received message from rabbitmq: {}", message);

        // Retrieve test cases
        List<TestCase> testCases = testCaseService.getTestCases(message.problemId());

        // Submit batch code to judge0
        List<SubmissionRequestBody> requestBodies = new ArrayList<>();
        for (TestCase testCase : testCases) {
            SubmissionRequestBody submissionRequestBody = new SubmissionRequestBody(
                    message.sourceCode(),
                    message.language(),
                    testCase.getInput(),
                    testCase.getOutput()
            );
            requestBodies.add(submissionRequestBody);
        }
        List<SubmissionToken> submissionTokens = submissionService.submitBatch(requestBodies);

        // Schedule fetcher task
        fetcher.scheduleFetching(message, submissionTokens);

        // Create submission
        submissionService.createSubmissionIfNotExists(
                message.userId(),
                message.contestId(),
                message.problemId(),
                null,
                message.language(),
                message.submittedAt()
        );
    }

}
