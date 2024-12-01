package ru.tbank.contestservice.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.contestservice.controller.ContestController;
import ru.tbank.contestservice.dto.contest.AddProblemContestRequest;
import ru.tbank.contestservice.dto.contest.ContestDTO;
import ru.tbank.contestservice.dto.contest.ContestResult;
import ru.tbank.contestservice.dto.contest.CreateContestRequest;
import ru.tbank.contestservice.dto.problem.ProblemDTO;
import ru.tbank.contestservice.service.ContestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contest")
public class ContestControllerImpl implements ContestController {

    private final ContestService contestService;

    @Override
    public ContestDTO getContestInfo(long contestId) {
        return contestService.getContestInfo(contestId);
    }

    @Override
    public List<ProblemDTO> getProblems(long contestId) {
        return contestService.getProblems(contestId);
    }

    @Override
    public ContestResult getContestResult(long contestId) {
        return contestService.getContestResults(contestId);
    }

    @Override
    public void createContest(CreateContestRequest createContestRequest) {
        contestService.createContest(createContestRequest);
    }

    @Override
    public void addProblem(AddProblemContestRequest addProblemContestRequest) {
        contestService.addProblem(
                addProblemContestRequest.contestId(),
                addProblemContestRequest.problemId()
        );
    }

    @Override
    public void registerForContest(long contestId) {
        contestService.registerUserForContest(contestId);
    }

}
