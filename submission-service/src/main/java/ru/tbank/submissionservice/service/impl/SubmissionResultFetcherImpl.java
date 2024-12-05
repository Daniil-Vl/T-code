package ru.tbank.submissionservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.submissionservice.dto.SubmissionResult;
import ru.tbank.submissionservice.dto.SubmissionToken;
import ru.tbank.submissionservice.service.SubmissionResultFetcher;
import ru.tbank.submissionservice.service.SubmissionService;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionResultFetcherImpl implements SubmissionResultFetcher {

    private final ScheduledExecutorService scheduledExecutorService;
    private final SubmissionService submissionService;

    @Override
    public void fetchSubmissionResults(long userId, long contestId, long problemId, List<SubmissionToken> tokens) {
        scheduledExecutorService.schedule(() -> {
            List<SubmissionResult> submissionResults = submissionService.getBatchSubmissionResult(tokens);

            int score = 0;
            String status = SubmissionResult.ACCEPTED_STATUS_MESSAGE;
            for (SubmissionResult submissionResult : submissionResults) {
                if (submissionResult.isAccepted()) {
                    score++;
                    continue;
                }

                // Get first non-accepted status message, if there are any
                if (status.equals(SubmissionResult.ACCEPTED_STATUS_MESSAGE)) {
                    status = submissionResult.status().description();
                }
            }

            log.info("Submission results fetched, score: {}, status: {}", score, status);
            // TODO: Save submission
        }, tokens.size(), TimeUnit.SECONDS);
    }
}
