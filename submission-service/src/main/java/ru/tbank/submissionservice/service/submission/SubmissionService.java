package ru.tbank.submissionservice.service.submission;

import ru.tbank.submissionservice.dto.submission.SubmissionRequestBody;
import ru.tbank.submissionservice.dto.submission.SubmissionResults;
import ru.tbank.submissionservice.dto.submission.SubmissionToken;
import ru.tbank.submissionservice.model.entities.SubmissionEntity;
import ru.tbank.submissionservice.model.entities.id.SubmissionId;

import java.time.OffsetDateTime;
import java.util.List;

public interface SubmissionService {

    /**
     * Retrieves submission entity, or throw SubmissionNotFoundException
     *
     * @param submissionId
     * @return
     */
    SubmissionEntity getSubmissionEntity(SubmissionId submissionId);

    /**
     * Create new submission, if submission with given userId, contestId, problemId not exists yet
     *
     * @param userId
     * @param contestId
     * @param problemId
     * @param codeKey
     * @param language
     * @param submittedAt
     * @return
     */
    SubmissionEntity createSubmissionIfNotExists(long userId, long contestId, long problemId, String codeKey, String language, OffsetDateTime submittedAt);

    /**
     * Submit batch to judge0
     *
     * @param requests
     * @return
     */
    List<SubmissionToken> submitBatch(List<SubmissionRequestBody> requests);

    /**
     * Retrieves submission results for multiple tokens
     *
     * @param tokens
     * @return
     */
    SubmissionResults getBatchSubmissionResult(List<SubmissionToken> tokens);

}
