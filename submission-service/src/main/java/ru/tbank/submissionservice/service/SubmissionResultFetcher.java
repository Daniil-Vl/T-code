package ru.tbank.submissionservice.service;

import ru.tbank.submissionservice.dto.SubmissionToken;

import java.util.List;

public interface SubmissionResultFetcher {
    void fetchSubmissionResults(long userId, long contestId, long problemId, List<SubmissionToken> tokens);
}
