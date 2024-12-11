package ru.tbank.submissionservice.client;

import ru.tbank.submissionservice.dto.submission.SubmissionRequestDTO;
import ru.tbank.submissionservice.dto.submission.SubmissionResults;
import ru.tbank.submissionservice.dto.submission.SubmissionToken;
import ru.tbank.submissionservice.dto.language.Language;

import java.util.List;

public interface Judge0Client {
    /**
     * Create batch submission and send it to judge0 for execution
     *
     * @param submissionRequests
     * @return
     */
    List<SubmissionToken> submitBatch(List<SubmissionRequestDTO> submissionRequests);

    /**
     * Retrieves results for tokens batch
     *
     * @param tokens
     * @return
     */
    SubmissionResults getBatchSubmissionResults(List<SubmissionToken> tokens);

    /**
     * Return available languages
     *
     * @return
     */
    List<Language> getLanguages();
}
