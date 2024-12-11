package ru.tbank.contestservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.contestservice.client.SubmissionServiceClient;
import ru.tbank.contestservice.dao.jpa.ContestRepository;
import ru.tbank.contestservice.dao.jpa.ProblemRepository;
import ru.tbank.contestservice.dto.contest.ContestDTO;
import ru.tbank.contestservice.dto.contest.ContestResult;
import ru.tbank.contestservice.dto.contest.CreateContestRequest;
import ru.tbank.contestservice.dto.contest.UserRating;
import ru.tbank.contestservice.dto.problem.ProblemDTO;
import ru.tbank.contestservice.dto.submission.SubmissionMessage;
import ru.tbank.contestservice.dto.submission.SubmissionRequest;
import ru.tbank.contestservice.exception.ForbiddenException;
import ru.tbank.contestservice.exception.contest.ContestNotFoundException;
import ru.tbank.contestservice.exception.contest.ContestTimeException;
import ru.tbank.contestservice.exception.problem.ProblemNotFoundException;
import ru.tbank.contestservice.model.entities.ContestEntity;
import ru.tbank.contestservice.model.entities.ProblemEntity;
import ru.tbank.contestservice.model.entities.UserEntity;
import ru.tbank.contestservice.service.ContestService;
import ru.tbank.contestservice.service.ProblemService;
import ru.tbank.contestservice.service.UserService;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContestServiceImpl implements ContestService {

    private final SubmissionServiceClient submissionServiceClient;
    private final ContestRepository contestRepository;
    private final ProblemService problemService;
    private final ProblemRepository problemRepository;
    private final UserService userService;


    @Override
    @Transactional
    public void createContest(CreateContestRequest createContestRequest) {
        log.info("Creating contest with title {}, start date {}, end date {}",
                createContestRequest.title(),
                createContestRequest.startTime(),
                createContestRequest.endTime()
        );
        UserEntity authenticatedUser = userService.getAuthenticatedUser();

        var contestBuilder = ContestEntity.builder()
                .title(createContestRequest.title())
                .startDateTime(createContestRequest.startTime())
                .endDateTime(createContestRequest.endTime())
                .owner(authenticatedUser);

        // Add problems to contest
        List<Long> problemIds = createContestRequest.problemIds();
        if (problemIds != null && !problemIds.isEmpty()) {
            List<ProblemEntity> foundedProblems = problemRepository.findAllById(problemIds);
            Set<Long> foundedIds = foundedProblems.stream()
                    .map(ProblemEntity::getId)
                    .collect(Collectors.toSet());

            for (long problemId : problemIds) {
                if (!foundedIds.contains(problemId)) {
                    log.warn("Problem with id {} not found", problemId);
                    throw new ProblemNotFoundException(
                            String.format("Problem with id %d not found", problemId)
                    );
                }
            }

            contestBuilder.problems(new HashSet<>(foundedProblems));
        }

        contestRepository.save(contestBuilder.build());
    }

    @Override
    @Transactional
    public void registerUserForContest(long contestId) {
        UserEntity authenticatedUser = userService.getAuthenticatedUser();
        log.info("Registering user {} on contest {}", authenticatedUser, contestId);

        ContestEntity contest = getContestEntity(contestId);

        OffsetDateTime now = OffsetDateTime.now();
        if (now.isAfter(contest.getStartDateTime())) {
            throw new ContestTimeException("Registration for the contest has already finished");
        }

        contest.getParticipants().add(authenticatedUser);

        contestRepository.save(contest);
    }

    @Override
    public ContestResult getContestResults(long contestId) {
        checkContestExistence(contestId);
        return submissionServiceClient.getContestResult(contestId);
    }

    @Override
    public UserRating getUserRating(long contestId) {
        checkContestExistence(contestId);
        return submissionServiceClient.getUserRating(contestId);
    }

    private void checkContestExistence(long contestId) {
        if (!contestRepository.existsById(contestId)) {
            throw new ContestNotFoundException(
                    String.format("Contest %d not found", contestId)
            );
        }
    }

    @Override
    public void submit(long contestId, long problemId, SubmissionRequest submissionRequest) {
        log.info("Creating submission with contest_id = {}, problem_id = {}, request = {}", contestId, problemId, submissionRequest);
        ContestEntity contestEntity = getContestEntity(contestId);

        OffsetDateTime now = OffsetDateTime.now();
        if (now.isBefore(contestEntity.getStartDateTime())) {
            throw new ContestTimeException("Contest not started yet");
        }

        if (now.isAfter(contestEntity.getEndDateTime())) {
            throw new ContestTimeException("Contest already finished");
        }

        UserEntity authenticatedUser = userService.getAuthenticatedUser();
        if (!contestEntity.getParticipants().contains(authenticatedUser)) {
            throw new ForbiddenException("User is not a participant in the contest");
        }

        ProblemEntity problemEntity = problemService.getProblemEntity(problemId);
        if (!contestEntity.getProblems().contains(problemEntity)) {
            throw new ProblemNotFoundException("Problem not found in contest");
        }

        submissionServiceClient.submit(
                new SubmissionMessage(
                        authenticatedUser.getId(),
                        contestId,
                        problemId,
                        submissionRequest.sourceCode(),
                        submissionRequest.language(),
                        OffsetDateTime.now()
                )
        );
    }

    @Override
    @Transactional
    public void addProblem(long contestId, long problemId) {
        log.info("Adding problem {} to contest {}", problemId, contestId);

        ContestEntity contest = getContestEntity(contestId);
        OffsetDateTime now = OffsetDateTime.now();
        if (now.isAfter(contest.getStartDateTime())) {
            throw new ContestTimeException("Contest already started");
        }

        UserEntity authenticatedUser = userService.getAuthenticatedUser();
        if (!contest.getOwner().equals(authenticatedUser)) {
            throw new ForbiddenException(
                    String.format("User %s not contest %d owner", authenticatedUser.getUsername(), contestId)
            );
        }

        ProblemEntity problemEntity = problemService.getProblemEntity(problemId);
        contest.getProblems().add(problemEntity);

        contestRepository.save(contest);
    }

    @Override
    public ContestEntity getContestEntity(long contestId) {
        return contestRepository.findById(contestId)
                .orElseThrow(() -> {
                    log.warn("Contest with id {} not found", contestId);
                    return new ContestNotFoundException(
                            String.format("Contest with id %d not found", contestId)
                    );
                });
    }

    @Override
    public ContestDTO getContestInfo(long contestId) {
        log.info("Getting contest info for contest {}", contestId);

        ContestEntity contestEntity = getContestEntity(contestId);
        return ContestDTO.fromEntity(contestEntity);
    }

    @Override
    public List<ProblemDTO> getProblems(long contestId) {
        log.info("Getting problems for contest {}", contestId);

        ContestEntity contestEntity = getContestEntity(contestId);
        return contestEntity.getProblems()
                .stream()
                .map(ProblemDTO::fromEntity)
                .toList();
    }
}
