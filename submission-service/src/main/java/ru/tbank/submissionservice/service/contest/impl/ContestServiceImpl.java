package ru.tbank.submissionservice.service.contest.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.submissionservice.dao.jpa.SubmissionRepository;
import ru.tbank.submissionservice.dto.contest.ContestResult;
import ru.tbank.submissionservice.dto.contest.UserRating;
import ru.tbank.submissionservice.enums.SubmissionStatus;
import ru.tbank.submissionservice.model.entities.SubmissionEntity;
import ru.tbank.submissionservice.service.contest.ContestService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContestServiceImpl implements ContestService {

    private final SubmissionRepository submissionRepository;

    @Override
    public ContestResult getContestResult(long contestId) {
        log.info("Trying to retrieve contest {} results", contestId);

        List<SubmissionEntity> submissions = submissionRepository.getAllByContestId(contestId);
        List<ContestResult.ProblemResult> problemResults = new ArrayList<>();

        for (SubmissionEntity submission : submissions) {
            var result = ContestResult.ProblemResult.fromEntity(submission);
            problemResults.add(result);
        }

        return new ContestResult(contestId, problemResults);
    }

    @Override
    public UserRating getUserRating(long contestId) {
        log.info("Trying to retrieve contest {} user ratings", contestId);

        List<SubmissionEntity> submissions = submissionRepository.getAllByContestId(contestId);
        Map<Long, UserRating.User> userMap = new HashMap<>();

        for (SubmissionEntity submission : submissions) {
            UserRating.User user = userMap.getOrDefault(
                    submission.getUserId(),
                    new UserRating.User(submission.getUserId())
            );

            if (SubmissionStatus.ACCEPTED.getValue().equals(submission.getStatus())) {
                user.setSolvedProblems(user.getSolvedProblems() + 1);
            }

            user.setTotalScore(user.getTotalScore() + submission.getScore());
            userMap.put(submission.getUserId(), user);
        }

        List<UserRating.User> users = new ArrayList<>(userMap.values());
        users.sort(UserRating.User::compareTo);

        return new UserRating(
                contestId,
                users
        );
    }
}
