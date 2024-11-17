package ru.tbank.submissionservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.submissionservice.dao.jpa.SubmissionRepository;
import ru.tbank.submissionservice.model.entities.SubmissionEntity;
import ru.tbank.submissionservice.model.entities.id.SubmissionId;
import ru.tbank.submissionservice.service.SubmissionService;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository repository;
    private final SubmissionRepository submissionRepository;

    @Override
    @Transactional
    public SubmissionEntity createSubmission(long userId, long contestId, long problemId, String codeKey, OffsetDateTime submittedAt) {
        SubmissionEntity submission = SubmissionEntity.builder()
                .userId(userId)
                .contestId(contestId)
                .problemId(problemId)
                .codeKey(codeKey)
                .submittedAt(submittedAt)
                .build();
        return repository.save(submission);
    }

    @Override
    @Transactional
    public void updateSubmission(SubmissionId submissionId, String status, int score, int executionTimeMs, int memoryUserKb) {
        submissionRepository.updateSubmission(
                submissionId.getUserId(),
                submissionId.getContestId(),
                submissionId.getProblemId(),
                status,
                score,
                executionTimeMs,
                memoryUserKb
        );
    }

}
