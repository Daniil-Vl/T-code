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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.tbank.submissionservice.client.Judge0Client;
import ru.tbank.submissionservice.dto.SubmissionRequestBody;
import ru.tbank.submissionservice.dto.SubmissionRequestDTO;
import ru.tbank.submissionservice.dto.SubmissionResult;
import ru.tbank.submissionservice.dto.SubmissionToken;
import ru.tbank.submissionservice.service.LanguageService;
import ru.tbank.submissionservice.service.SubmissionService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository repository;
    private final SubmissionRepository submissionRepository;
    private final Judge0Client judge0Client;
    private final LanguageService languageService;

    @Override
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

    @Override
    public SubmissionToken submit(String sourceCode, String language, String stdin) {
        return judge0Client.submit(
                sourceCode,
                languageService.getLanguageIdByLanguageName(language),
                stdin
        );
    }

    @Override
    public Mono<SubmissionResult> submitWaiting(String sourceCode, String language, String stdin) {
        return judge0Client.submitWaiting(
                sourceCode,
                languageService.getLanguageIdByLanguageName(language),
                stdin
        );
    }

    @Override
    public List<SubmissionToken> submitBatch(List<SubmissionRequestBody> requests) {
        // Convert language name to language id in every submission request
        List<SubmissionRequestDTO> requestDTOList = requests
                .stream()
                .map(
                        request -> new SubmissionRequestDTO(
                                request.sourceCode(),
                                languageService.getLanguageIdByLanguageName(request.language()),
                                request.stdin()
                        )
                )
                .toList();

        return judge0Client.submitBatch(requestDTOList);
    }

    @Override
    public List<SubmissionResult> getBatchSubmissionResult(List<SubmissionToken> tokens) {
        return judge0Client.getBatchSubmissionResults(tokens);
    }

}
