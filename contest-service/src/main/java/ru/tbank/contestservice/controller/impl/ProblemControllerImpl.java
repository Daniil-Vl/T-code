package ru.tbank.contestservice.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.contestservice.controller.ProblemController;
import ru.tbank.contestservice.dto.TestCase;
import ru.tbank.contestservice.dto.problem.ProblemDTO;
import ru.tbank.contestservice.service.ProblemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/problem")
public class ProblemControllerImpl implements ProblemController {

    private final ProblemService problemService;

    @Override
    public void createProblem(ProblemDTO problemDTO) {
        problemService.createProblem(problemDTO);
    }

    @Override
    public ProblemDTO getProblemById(long problemId) {
        return problemService.getProblem(problemId);
    }

    @Override
    public ProblemDTO getProblemDescriptionById(long problemId) {
        return problemService.getProblemDescription(problemId);
    }

    @Override
    public void updateProblemDescription(long problemId, String description) {
        problemService.updateProblemDescription(problemId, description);
    }

    @Override
    public void updateProblemTestCases(long problemId, List<TestCase> testCases) {
        problemService.updateProblemTestCases(problemId, testCases);
    }

}
