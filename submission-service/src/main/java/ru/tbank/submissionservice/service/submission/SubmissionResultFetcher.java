package ru.tbank.submissionservice.service.submission;

import ru.tbank.submissionservice.dto.submission.SubmissionMessage;
import ru.tbank.submissionservice.dto.submission.SubmissionToken;

import java.util.List;

public interface SubmissionResultFetcher {

    /**
     * Schedules submission results fetching task, which retrieves submission results for given tokens from judge0
     *
     * @param submissionMessage
     * @param tokens
     */
    void scheduleFetching(SubmissionMessage submissionMessage, List<SubmissionToken> tokens);
}
