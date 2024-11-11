package ru.tbank.submissionservice.client;

import reactor.core.publisher.Mono;
import ru.tbank.submissionservice.dto.SubmissionRequestDTO;
import ru.tbank.submissionservice.dto.SubmissionResult;
import ru.tbank.submissionservice.dto.SubmissionToken;
import ru.tbank.submissionservice.enums.Language;

import java.util.List;

public interface Judge0Client {
    SubmissionToken submit(String sourceCode, int languageId, String stdin);

    Mono<SubmissionResult> submitWaiting(String sourceCode, int languageId, String stdin);

    List<SubmissionToken> submitBatch(List<SubmissionRequestDTO> submissionRequests);

    SubmissionResult getSubmissionResult(String submissionToken);

    List<Language> getLanguages();
}
