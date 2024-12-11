package ru.tbank.submissionservice.service.contest;

import ru.tbank.submissionservice.dto.contest.ContestResult;
import ru.tbank.submissionservice.dto.contest.UserRating;

public interface ContestService {

    /**
     * Return all submissions for given contest
     *
     * @param contestId
     * @return
     */
    ContestResult getContestResult(long contestId);

    /**
     * Return rating for given contest
     * Users sorted by number of solved tasks and overall score
     *
     * @param contestId
     * @return
     */
    UserRating getUserRating(long contestId);

}
