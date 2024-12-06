package ru.tbank.contestservice.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.contestservice.client.SubmissionServiceClient;
import ru.tbank.contestservice.dao.jpa.ProblemRepository;
import ru.tbank.contestservice.dao.jpa.TestCaseRepository;
import ru.tbank.contestservice.dto.TestCase;
import ru.tbank.contestservice.dto.problem.ProblemDTO;
import ru.tbank.contestservice.exception.ForbiddenException;
import ru.tbank.contestservice.exception.problem.ProblemNotFoundException;
import ru.tbank.contestservice.model.entities.ProblemEntity;
import ru.tbank.contestservice.model.entities.UserEntity;
import ru.tbank.contestservice.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class ProblemServiceImplTest {

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private SubmissionServiceClient submissionServiceClient;

    @Mock
    private TestCaseRepository testCaseRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProblemServiceImpl problemServiceImpl;


    // Create problem without tests
    @Test
    void givenProblemDTOWithoutTests_whenCreateProblem_thenSaveProblemAndDoNotSaveTests() {
        // Assign
        UserEntity user = new UserEntity();
        ProblemDTO problemDTO = new ProblemDTO("title", "description", List.of());
        Mockito.when(userService.getAuthenticatedUser()).thenReturn(user);

        // Act
        problemServiceImpl.createProblem(problemDTO);

        // Assert
        Mockito.verify(problemRepository).save(
                ProblemEntity.builder()
                        .title(problemDTO.title())
                        .description(problemDTO.description())
                        .owner(user)
                        .build()
        );
        Mockito.verify(submissionServiceClient, Mockito.times(0))
                .saveTestCases(Mockito.anyLong(), Mockito.anyList());
        Mockito.verify(testCaseRepository, Mockito.times(0))
                .saveAll(Mockito.anyList());
    }

    // Create problem with tests
    @Test
    void givenProblemDTOWithTests_whenCreateProblem_thenSaveProblemAndDoSaveTests() {
        // Assign
        UserEntity user = new UserEntity();
        ProblemDTO problemDTO = new ProblemDTO("title", "description", List.of(new TestCase("input", "output")));
        Mockito.when(
                userService.getAuthenticatedUser()
        ).thenReturn(
                user
        );

        Mockito.when(
                problemRepository.save(Mockito.any(ProblemEntity.class))
        ).thenReturn(
                ProblemEntity.builder().id(1).title("title").build()
        );

        Mockito.when(
                submissionServiceClient.saveTestCases(Mockito.anyLong(), Mockito.anyList())
        ).thenReturn(
                // Simulate slow executions (sending to S3)
                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
        );

        // Act
        problemServiceImpl.createProblem(problemDTO);

        // Assert
        Mockito.verify(problemRepository).save(
                ProblemEntity.builder()
                        .title(problemDTO.title())
                        .description(problemDTO.description())
                        .owner(user)
                        .build()
        );
        Mockito.verify(submissionServiceClient, Mockito.times(1))
                .saveTestCases(Mockito.anyLong(), Mockito.anyList());
        Mockito.verify(testCaseRepository, Mockito.times(1))
                .saveAll(Mockito.anyList());
    }

    // Retrieving non-existent problem
    @Test
    void givenNonExistentProblemId_whenGetProblemEntity_thenThrowProblemNotFoundException() {
        // Assign
        long problemId = 1L;

        Mockito.when(
                problemRepository.findById(problemId)
        ).thenReturn(
                Optional.empty()
        );

        // Act + Assert
        Assertions.assertThrows(
                ProblemNotFoundException.class,
                () -> problemServiceImpl.getProblemEntity(problemId)
        );
    }

    // Retrieving problem with tests
    @Test
    void givenUserOwner_whenGetProblem_thenRetrieveProblemWithTests() {
        // Assign
        UserEntity user = new UserEntity(2, "username", "password", null);
        ProblemEntity problemEntity = ProblemEntity.builder()
                .id(1)
                .owner(user)
                .build();

        Mockito.when(
                problemRepository.findById(problemEntity.getId())
        ).thenReturn(
                Optional.of(problemEntity)
        );

        Mockito.when(
                userService.getAuthenticatedUser()
        ).thenReturn(
                user
        );

        // Act
        ProblemDTO problem = problemServiceImpl.getProblem(problemEntity.getId());

        // Assert
        Assertions.assertEquals(ProblemDTO.fromEntity(problemEntity), problem);
        Mockito.verify(submissionServiceClient).getTestCases(problemEntity.getId());
    }

    // Retrieving problem, when you aren't owner
    @Test
    void givenUserNotOwner_whenGetProblem_thenThrowForbiddenException() {
        // Assign
        UserEntity user = new UserEntity(2, "username", "password", null);
        ProblemEntity problemEntity = ProblemEntity.builder()
                .id(1)
                .owner(new UserEntity(1, "username", "password", "email"))
                .build();

        Mockito.when(
                problemRepository.findById(problemEntity.getId())
        ).thenReturn(
                Optional.of(problemEntity)
        );

        Mockito.when(
                userService.getAuthenticatedUser()
        ).thenReturn(
                user
        );

        // Act + Assert
        Assertions.assertThrows(
                ForbiddenException.class,
                () -> problemServiceImpl.getProblem(problemEntity.getId())
        );
    }

    // Update test cases replace old
    @Test
    void givenProblemId_whenUpdateTestCases_thenReplaceOldTestCases() {
        // Assign
        long problemId = 1L;
        UserEntity user = new UserEntity(2, "username", "password", null);
        ProblemEntity problemEntity = ProblemEntity.builder()
                .id(1)
                .owner(user)
                .build();
        List<TestCase> testCases = List.of(new TestCase("input", "output"));

        Mockito.when(
                problemRepository.findById(problemEntity.getId())
        ).thenReturn(
                Optional.of(problemEntity)
        );

        Mockito.when(
                userService.getAuthenticatedUser()
        ).thenReturn(
                user
        );

        Mockito.when(
                submissionServiceClient.updateTestCases(Mockito.anyLong(), Mockito.anyList())
        ).thenReturn(
                // Simulate slow executions (sending to S3)
                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
        );

        // Act
        problemServiceImpl.updateProblemTestCases(problemId, testCases);

        // Assert
        Mockito.verify(submissionServiceClient).updateTestCases(Mockito.anyLong(), Mockito.anyList());
        Mockito.verify(testCaseRepository).removeAllByProblem(Mockito.any(ProblemEntity.class));
        Mockito.verify(testCaseRepository).saveAll(Mockito.anyList());
    }

}