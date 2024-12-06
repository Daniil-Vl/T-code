package ru.tbank.contestservice.client;

import ru.tbank.contestservice.dto.TestCase;
import ru.tbank.contestservice.dto.contest.ContestResult;
import ru.tbank.contestservice.dto.submission.SubmissionMessage;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface SubmissionServiceClient {
    Set<String> getAvailableLanguages();

    /**
     * Start processing of user`s submission
     *
     * @param submissionMessage
     */
    void submit(SubmissionMessage submissionMessage);

    /**
     * Save test cases for s3 storage
     *
     * @param problemId
     * @param testCases
     * @return
     */
    CompletableFuture<Void> saveTestCases(long problemId, List<TestCase> testCases);

    /**
     * Update test cases in s3 storage
     *
     * @param problemId
     * @param testCases
     * @return
     */
    CompletableFuture<Void> updateTestCases(long problemId, List<TestCase> testCases);

    /**
     * Retrieve test cases from s3 storage
     *
     * @param problemId
     * @return
     */
    List<TestCase> getTestCases(long problemId);

    /**
     * Retrieve the current rating table of the contest
     *
     * @param contestId
     * @return
     */
    ContestResult getContestResult(long contestId);
}
