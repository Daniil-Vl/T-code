package ru.tbank.contestservice.service;

import ru.tbank.contestservice.dto.TestCase;
import ru.tbank.contestservice.dto.problem.ProblemDTO;
import ru.tbank.contestservice.model.entities.ProblemEntity;

import java.util.List;

public interface ProblemService {

    /**
     * Create problem entity in db with test cases entity and upload test cases to object storage
     *
     * @param problemDTO
     */
    void createProblem(ProblemDTO problemDTO);

    /**
     * Return problem without tests
     *
     * @param problemId
     * @return
     */
    ProblemDTO getProblemDescription(long problemId);

    /**
     * Get problem with tests
     *
     * @param problemId
     * @return
     */
    ProblemDTO getProblem(long problemId);

    /**
     * Update problem
     *
     * @param problemId
     * @param description
     */
    void updateProblemDescription(long problemId, String description);

    /**
     * Update problem`s test cases
     *
     * @param problemId
     * @param testCases
     */
    void updateProblemTestCases(long problemId, List<TestCase> testCases);

    /**
     * Get problem entity by id
     *
     * @param problemId
     * @return
     */
    ProblemEntity getProblemEntity(long problemId);
}
