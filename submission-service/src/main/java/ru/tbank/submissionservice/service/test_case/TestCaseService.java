package ru.tbank.submissionservice.service.test_case;

import ru.tbank.submissionservice.dto.test_case.TestCase;

import java.io.IOException;
import java.util.List;

public interface TestCaseService {

    /**
     * Retrieves test cases from given problem from s3 object storage
     *
     * @param problemId
     * @return
     */
    List<TestCase> getTestCases(long problemId) throws IOException;

    /**
     * Save test cases to s3 object storage for given problem
     *
     * @param problemId
     * @param testCases
     */
    void saveTestCases(long problemId, List<TestCase> testCases) throws IOException;

}
