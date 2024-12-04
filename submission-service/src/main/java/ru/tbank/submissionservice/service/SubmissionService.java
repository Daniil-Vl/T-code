package ru.tbank.submissionservice.service;

import ru.tbank.submissionservice.model.entities.SubmissionEntity;
import ru.tbank.submissionservice.model.entities.id.SubmissionId;

import java.time.OffsetDateTime;

public interface SubmissionService {
    SubmissionEntity createSubmission(long userId, long contestId, long problemId, String codeKey, OffsetDateTime submittedAt);

    void updateSubmission(SubmissionId submissionId, String status, int score, int executionTimeMs, int memoryUserKb);
}
