package ru.tbank.submissionservice.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.submissionservice.controller.TestCaseController;
import ru.tbank.submissionservice.dto.test_case.TestCase;
import ru.tbank.submissionservice.service.test_case.TestCaseService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test-case")
public class TestCaseControllerImpl implements TestCaseController {

    private final TestCaseService testCaseService;

    @Override
    public List<TestCase> getTestCases(long problemId) throws IOException {
        return testCaseService.getTestCases(problemId);
    }

    @Override
    public void saveTestCases(long problemId, List<TestCase> testCases) throws IOException {
        testCaseService.saveTestCases(problemId, testCases);
    }

}
