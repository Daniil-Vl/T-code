package ru.tbank.submissionservice.service;

import jakarta.transaction.Transactional;
import ru.tbank.submissionservice.model.entities.SubmissionEntity;
import ru.tbank.submissionservice.model.entities.id.SubmissionId;

import java.time.OffsetDateTime;

public interface SubmissionService {
    @Transactional
    SubmissionEntity createSubmission(long userId, long contestId, long problemId, String codeKey, OffsetDateTime submittedAt);

    @Transactional
    void updateSubmission(SubmissionId submissionId, String status, int score, int executionTimeMs, int memoryUserKb);
}
