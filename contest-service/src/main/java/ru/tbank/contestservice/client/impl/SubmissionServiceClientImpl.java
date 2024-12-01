package ru.tbank.contestservice.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.tbank.contestservice.client.SubmissionServiceClient;
import ru.tbank.contestservice.dto.TestCase;
import ru.tbank.contestservice.dto.contest.ContestResult;
import ru.tbank.contestservice.dto.submission.SubmissionMessage;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionServiceClientImpl implements SubmissionServiceClient {

    private final WebClient webClient;

    @Override
    @Cacheable(cacheResolver = "languageCacheResolver")
    public Set<String> getAvailableLanguages() {
        log.info("Retrieve available languages from submission service");
        throw new UnsupportedOperationException();
    }

    @Override
    public void submit(SubmissionMessage submissionMessage) {
        log.info("Create submission: {}", submissionMessage);
        throw new UnsupportedOperationException();
    }

    @Async
    @Override
    public CompletableFuture<Void> saveTestCases(long problemId, List<TestCase> testCases) {
        log.info("Save test cases: {}", testCases);
        throw new UnsupportedOperationException();
    }

    @Async
    @Override
    public CompletableFuture<Void> updateTestCases(long problemId, List<TestCase> testCases) {
        log.info("Update test cases: {}", testCases);
        throw new UnsupportedOperationException();
    }

    @Override
    public List<TestCase> getTestCases(long problemId) {
        log.info("Request test cases for problem with id {} from submission service", problemId);
        throw new UnsupportedOperationException();
    }

    @Override
    public ContestResult getContestResult(long contestId) {
        log.info("Requesting results of contest {} from submission service", contestId);
        throw new UnsupportedOperationException();
    }

}
