package ru.tbank.submissionservice.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.submissionservice.controller.ContestController;
import ru.tbank.submissionservice.dto.contest.ContestResult;
import ru.tbank.submissionservice.dto.contest.UserRating;
import ru.tbank.submissionservice.service.contest.ContestService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ContestControllerImpl implements ContestController {

    private final ContestService contestService;

    @Override
    public ContestResult getContestResult(long contestId) {
        return contestService.getContestResult(contestId);
    }

    @Override
    public UserRating getUserRating(long contestId) {
        return contestService.getUserRating(contestId);
    }

}
