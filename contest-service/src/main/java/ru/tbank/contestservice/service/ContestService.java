package ru.tbank.contestservice.service;

import ru.tbank.contestservice.dto.contest.ContestDTO;
import ru.tbank.contestservice.dto.contest.ContestResult;
import ru.tbank.contestservice.dto.contest.CreateContestRequest;
import ru.tbank.contestservice.dto.problem.ProblemDTO;
import ru.tbank.contestservice.dto.submission.SubmissionRequest;
import ru.tbank.contestservice.model.entities.ContestEntity;

import java.util.List;

public interface ContestService {
    /**
     * Creates new contest with given configuration
     *
     * @param createContestRequest
     */
    void createContest(CreateContestRequest createContestRequest);

    /**
     * Register user as contest participant
     *
     * @param contestId
     */
    void registerUserForContest(long contestId);

    /**
     * Get current contest scores
     *
     * @param contestId
     * @return
     */
    ContestResult getContestResults(long contestId);

    /**
     * Submit user`s code for testing in submission service
     *
     * @param contestId
     * @param problemId
     * @param submissionRequest
     */
    void submit(long contestId, long problemId, SubmissionRequest submissionRequest);

    /**
     * Add problem to contest
     *
     * @param contestId
     * @param problemId
     */
    void addProblem(long contestId, long problemId);

    /**
     * Get contest entity by id
     *
     * @param contestId
     * @return
     */
    ContestEntity getContestEntity(long contestId);


    /**
     * Get contest information
     *
     * @param contestId
     * @return
     */
    ContestDTO getContestInfo(long contestId);

    /**
     * Retrieves problems from given contest
     *
     * @param contestId
     * @return
     */
    List<ProblemDTO> getProblems(long contestId);
}
