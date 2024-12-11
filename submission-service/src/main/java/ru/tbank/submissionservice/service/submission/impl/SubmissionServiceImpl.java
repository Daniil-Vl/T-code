package ru.tbank.submissionservice.service.submission.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.submissionservice.client.Judge0Client;
import ru.tbank.submissionservice.dao.jpa.SubmissionRepository;
import ru.tbank.submissionservice.dto.submission.SubmissionRequestBody;
import ru.tbank.submissionservice.dto.submission.SubmissionRequestDTO;
import ru.tbank.submissionservice.dto.submission.SubmissionResults;
import ru.tbank.submissionservice.dto.submission.SubmissionToken;
import ru.tbank.submissionservice.enums.SubmissionStatus;
import ru.tbank.submissionservice.exception.submission.SubmissionNotFoundException;
import ru.tbank.submissionservice.model.entities.SubmissionEntity;
import ru.tbank.submissionservice.model.entities.id.SubmissionId;
import ru.tbank.submissionservice.service.language.LanguageService;
import ru.tbank.submissionservice.service.submission.SubmissionService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository repository;
    private final SubmissionRepository submissionRepository;
    private final Judge0Client judge0Client;
    private final LanguageService languageService;

    @Override
    public SubmissionEntity getSubmissionEntity(SubmissionId submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> {
                    log.warn("Submission with id {} not found", submissionId);
                    return new SubmissionNotFoundException(
                            String.format("Submission with id = %s not found", submissionId)
                    );
                });
    }

    @Override
    public SubmissionEntity createSubmissionIfNotExists(long userId, long contestId, long problemId, String codeKey, String language, OffsetDateTime submittedAt) {
        SubmissionId submissionId = new SubmissionId(userId, contestId, problemId);
        Optional<SubmissionEntity> optionalSubmission = submissionRepository.findById(submissionId);
        if (optionalSubmission.isPresent()) {
            return optionalSubmission.get();
        }

        SubmissionEntity submission = SubmissionEntity.builder()
                .userId(userId)
                .contestId(contestId)
                .problemId(problemId)
                .codeKey(codeKey)
                .language(language)
                .status(SubmissionStatus.PROCESSING.getValue())
                .submittedAt(submittedAt)
                .build();
        return repository.save(submission);
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
                                request.stdin(),
                                request.expectedOutput()
                        )
                )
                .toList();

        return judge0Client.submitBatch(requestDTOList);
    }

    @Override
    public SubmissionResults getBatchSubmissionResult(List<SubmissionToken> tokens) {
        return judge0Client.getBatchSubmissionResults(tokens);
    }

}
