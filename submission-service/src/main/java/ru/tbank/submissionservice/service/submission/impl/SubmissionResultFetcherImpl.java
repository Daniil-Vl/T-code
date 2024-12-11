package ru.tbank.submissionservice.service.submission.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.submissionservice.dao.jpa.SubmissionRepository;
import ru.tbank.submissionservice.dto.submission.SubmissionMessage;
import ru.tbank.submissionservice.dto.submission.SubmissionResult;
import ru.tbank.submissionservice.dto.submission.SubmissionResults;
import ru.tbank.submissionservice.dto.submission.SubmissionToken;
import ru.tbank.submissionservice.enums.SubmissionStatus;
import ru.tbank.submissionservice.exception.submission.SubmissionInProcessException;
import ru.tbank.submissionservice.model.entities.SubmissionEntity;
import ru.tbank.submissionservice.model.entities.id.SubmissionId;
import ru.tbank.submissionservice.service.source_code.SourceCodeService;
import ru.tbank.submissionservice.service.submission.SubmissionResultFetcher;
import ru.tbank.submissionservice.service.submission.SubmissionService;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionResultFetcherImpl implements SubmissionResultFetcher {

    private final ScheduledExecutorService scheduledExecutorService;
    private final SubmissionService submissionService;
    private final SourceCodeService sourceCodeService;
    private final SubmissionRepository submissionRepository;

    @Override
    public void scheduleFetching(SubmissionMessage submissionMessage, List<SubmissionToken> tokens) {
        SubmissionId submissionId = new SubmissionId(
                submissionMessage.userId(),
                submissionMessage.contestId(),
                submissionMessage.problemId()
        );
        log.info("Schedule submission result fetching from judge0 task for submission with id = {}", submissionId);
        log.info("Submission tokens: {}", tokens);
        scheduledExecutorService.schedule(
                new FetchingTask(submissionMessage, tokens),
                tokens.size(),
                TimeUnit.SECONDS
        );
    }

    @RequiredArgsConstructor
    private class FetchingTask implements Runnable {

        private static final int SECOND_TO_MS = 1000;

        private final SubmissionMessage submissionMessage;
        private final List<SubmissionToken> tokens;

        // If at least one submission in process, schedule task again
        @Override
        public void run() {
            SubmissionId submissionId = new SubmissionId(
                    submissionMessage.userId(),
                    submissionMessage.contestId(),
                    submissionMessage.problemId()
            );
            log.info("Start fetching submission results for submission with id = {}", submissionId);

            SubmissionResults submissionResults = submissionService.getBatchSubmissionResult(tokens);
            SubmissionStats stats;

            // If at least one submission execution net finished yet, then schedule fetching task again
            try {
                stats = getStats(submissionResults);
            } catch (SubmissionInProcessException e) {
                log.warn("Schedule submission task again for submission with id = {}", submissionId);
                scheduleFetching(submissionMessage, tokens);
                return;
            }

            // Update submission, only if current result better than previous
            SubmissionEntity submissionEntity = submissionService.getSubmissionEntity(submissionId);
            if (stats.score() >= submissionEntity.getScore()) {
                String codeKey = sourceCodeService.uploadSourceCode(submissionId, submissionMessage.sourceCode());

                submissionEntity.setScore(stats.score());
                submissionEntity.setExecutionTimeMs(stats.runningTimeSeconds());
                submissionEntity.setMemoryUsedKb(stats.runningMemoryKB());
                submissionEntity.setCodeKey(codeKey);
                submissionEntity.setStatus(stats.status());
                submissionEntity.setSubmittedAt(submissionMessage.submittedAt());
            }
            submissionRepository.save(submissionEntity);
        }

        /**
         * Calculate stats for submission results
         *
         * @param submissionResults, from which stats will be collected
         * @return stats
         * @throws SubmissionInProcessException, if at least one submission still being processed
         */
        private SubmissionStats getStats(SubmissionResults submissionResults) throws SubmissionInProcessException {
            int score = 0;
            int maxExecutionTimeMs = 0;
            int maxMemoryUsedKb = 0;
            String status = SubmissionStatus.ACCEPTED.getValue();

            for (SubmissionResult submissionResult : submissionResults.submissions()) {
                if (submissionResult.isInProcess()) {
                    throw new SubmissionInProcessException("Submission still being processed");
                }

                maxExecutionTimeMs = (int) Math.max(
                        maxExecutionTimeMs,
                        Math.round(submissionResult.runningTimeSeconds() * SECOND_TO_MS)
                );
                maxMemoryUsedKb = (int) Math.max(
                        maxMemoryUsedKb,
                        Math.round(submissionResult.runningMemoryKB())
                );

                // If one test failed, then stop checking other tests
                if (!submissionResult.isAccepted()) {
                    status = submissionResult.status().description();
                    break;
                }

                score++;
            }

            log.info("Submission results fetched, score: {}, status: {}", score, status);
            return new SubmissionStats(score, status, maxExecutionTimeMs, maxMemoryUsedKb);
        }

        private record SubmissionStats(int score, String status, int runningTimeSeconds, int runningMemoryKB) {
        }

    }
}
