package ru.tbank.contestservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbank.contestservice.client.SubmissionServiceClient;
import ru.tbank.contestservice.dao.jpa.ProblemRepository;
import ru.tbank.contestservice.dao.jpa.TestCaseRepository;
import ru.tbank.contestservice.dto.TestCase;
import ru.tbank.contestservice.dto.problem.ProblemDTO;
import ru.tbank.contestservice.exception.ForbiddenException;
import ru.tbank.contestservice.exception.problem.ProblemNotFoundException;
import ru.tbank.contestservice.model.entities.ProblemEntity;
import ru.tbank.contestservice.model.entities.TestCaseEntity;
import ru.tbank.contestservice.model.entities.UserEntity;
import ru.tbank.contestservice.service.ProblemService;
import ru.tbank.contestservice.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final SubmissionServiceClient submissionServiceClient;
    private final TestCaseRepository testCaseRepository;
    private final UserService userService;

    @Override
    @Transactional
    public void createProblem(ProblemDTO problemDTO) {
        UserEntity authenticatedUser = userService.getAuthenticatedUser();

        ProblemEntity problemEntity = ProblemEntity.builder()
                .title(problemDTO.title())
                .description(problemDTO.description())
                .owner(authenticatedUser)
                .build();
        problemEntity = problemRepository.save(problemEntity);

        if (problemDTO.testCases() != null && !problemDTO.testCases().isEmpty()) {
            // Send test cases to submission-service, where them will be saved to S3
            var savingTestCasesFuture = submissionServiceClient
                    .saveTestCases(problemEntity.getId(), problemDTO.testCases());

            // Save test cases info into db
            saveTestCaseEntities(problemEntity, problemDTO.testCases());
            savingTestCasesFuture.join();
        }

        log.info("Created problem {}", problemDTO);
    }

    @Override
    public ProblemDTO getProblemDescription(long problemId) {
        ProblemEntity problemEntity = getProblemEntity(problemId);
        return new ProblemDTO(
                problemId, problemEntity.getTitle(), problemEntity.getDescription(), List.of()
        );
    }

    @Override
    public ProblemDTO getProblem(long problemId) {
        ProblemEntity problemEntity = getProblemIfHasAccess(problemId);
        List<TestCase> testCases = submissionServiceClient.getTestCases(problemId);
        return new ProblemDTO(
                problemId, problemEntity.getTitle(), problemEntity.getDescription(), testCases
        );
    }

    @Override
    @Transactional
    public void updateProblemDescription(long problemId, String description) {
        ProblemEntity problemEntity = getProblemIfHasAccess(problemId);
        problemEntity.setDescription(description);
        problemRepository.save(problemEntity);
    }

    @Override
    @Transactional
    public void updateProblemTestCases(long problemId, List<TestCase> testCases) {
        var updateFuture = submissionServiceClient.updateTestCases(problemId, testCases);

        ProblemEntity problemEntity = getProblemIfHasAccess(problemId);
        testCaseRepository.removeAllByProblem(problemEntity);

        saveTestCaseEntities(problemEntity, testCases);
        updateFuture.join();
    }

    @Override
    public ProblemEntity getProblemEntity(long problemId) {
        return problemRepository.findById(problemId)
                .orElseThrow(() -> {
                    log.warn("Problem with id {} not found", problemId);
                    return new ProblemNotFoundException(String.format("Problem with id %d not found", problemId));
                });
    }

    private ProblemEntity getProblemIfHasAccess(long problemId) {
        ProblemEntity problemEntity = getProblemEntity(problemId);
        UserEntity authenticatedUser = userService.getAuthenticatedUser();

        if (!problemEntity.getOwner().equals(authenticatedUser)) {
            log.warn("User {} tried to get access to problem {}, but he isn't owner", authenticatedUser, problemEntity);
            throw new ForbiddenException(
                    String.format("User %s not problem %d owner", authenticatedUser.getUsername(), problemId)
            );
        }

        return problemEntity;
    }

    private void saveTestCaseEntities(ProblemEntity problemEntity, List<TestCase> testCases) {
        List<TestCaseEntity> testCaseEntities = new ArrayList<>();

        for (int testCaseNumber = 1; testCaseNumber <= testCases.size(); testCaseNumber++) {
            TestCaseEntity testCaseEntity = TestCaseEntity.builder()
                    .problem(problemEntity)
                    .number(testCaseNumber)
                    .build();
            testCaseEntities.add(testCaseEntity);
        }

        testCaseRepository.saveAll(testCaseEntities);
    }

}
