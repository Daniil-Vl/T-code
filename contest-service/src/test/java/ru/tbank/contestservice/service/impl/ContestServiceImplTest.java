package ru.tbank.contestservice.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.contestservice.client.SubmissionServiceClient;
import ru.tbank.contestservice.dao.jpa.ContestRepository;
import ru.tbank.contestservice.dao.jpa.ProblemRepository;
import ru.tbank.contestservice.dto.contest.CreateContestRequest;
import ru.tbank.contestservice.dto.submission.SubmissionMessage;
import ru.tbank.contestservice.dto.submission.SubmissionRequest;
import ru.tbank.contestservice.exception.ForbiddenException;
import ru.tbank.contestservice.exception.contest.ContestTimeException;
import ru.tbank.contestservice.exception.problem.ProblemNotFoundException;
import ru.tbank.contestservice.model.entities.ContestEntity;
import ru.tbank.contestservice.model.entities.ProblemEntity;
import ru.tbank.contestservice.model.entities.UserEntity;
import ru.tbank.contestservice.service.ProblemService;
import ru.tbank.contestservice.service.UserService;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class ContestServiceImplTest {

    @Mock
    private SubmissionServiceClient submissionServiceClient;

    @Mock
    private ContestRepository contestRepository;

    @Mock
    private ProblemService problemService;

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ContestServiceImpl contestService;

    // Successful contest creation
    @Test
    void givenCreateContestRequest_whenCreateContest_thenSuccessfullyCreateContest() {
        // Assign
        CreateContestRequest request = new CreateContestRequest(
                "title",
                OffsetDateTime.now(),
                OffsetDateTime.now().plusHours(3),
                List.of(1L)
        );
        UserEntity userEntity = new UserEntity(1L, "username", "password", "email");
        Set<ProblemEntity> problems = Set.of(
                new ProblemEntity(1L, "title", "description", userEntity)
        );
        ContestEntity expectedContest = ContestEntity.builder()
                .title("title")
                .startDateTime(request.startTime())
                .endDateTime(request.endTime())
                .owner(userEntity)
                .problems(problems)
                .build();

        Mockito.when(
                userService.getAuthenticatedUser()
        ).thenReturn(
                userEntity
        );

        Mockito.when(
                problemRepository.findAllById(request.problemIds())
        ).thenReturn(
                problems.stream().toList()
        );

        // Act
        contestService.createContest(request);

        // Assert
        Mockito.verify(contestRepository).save(expectedContest);
    }

    // Creation failed, because problem not found
    // ProblemNotFound exception thrown
    @Test
    void givenCreateContestRequest_whenCreateContestAndProblemNotFound_thenThrowsProblemNotFoundException() {
        // Assign
        CreateContestRequest request = new CreateContestRequest(
                "title",
                OffsetDateTime.now(),
                OffsetDateTime.now().plusHours(3),
                List.of(2L)
        );
        UserEntity userEntity = new UserEntity(1L, "username", "password", "email");

        Mockito.when(
                userService.getAuthenticatedUser()
        ).thenReturn(
                userEntity
        );

        Mockito.when(
                problemRepository.findAllById(request.problemIds())
        ).thenReturn(
                List.of()
        );

        // Act + Assert
        Assertions.assertThrows(
                ProblemNotFoundException.class,
                () -> contestService.createContest(request)
        );
    }

    // Success registration for contest
    @Test
    void givenContestIdAndUser_whenRegisterForContest_thenSuccessfullyUserRegistered() {
        // Assign
        long contestId = 1L;
        UserEntity userEntity = new UserEntity(1L, "username", "password", "email");
        Set<UserEntity> participants = Mockito.spy(new HashSet<>());
        ContestEntity contestEntity = ContestEntity.builder()
                .id(1L)
                .title("title")
                .startDateTime(OffsetDateTime.now().plusHours(1))
                .endDateTime(OffsetDateTime.now().plusHours(2))
                .problems(new HashSet<>())
                .participants(participants)
                .build();

        Mockito.when(
                userService.getAuthenticatedUser()
        ).thenReturn(
                userEntity
        );

        Mockito.when(
                contestRepository.findById(contestEntity.getId())
        ).thenReturn(
                Optional.of(contestEntity)
        );

        // Act
        contestService.registerUserForContest(contestId);

        // Assert
        Mockito.verify(participants).add(userEntity);
        Mockito.verify(contestRepository).save(contestEntity);
    }

    // Registration failed because contest already started
    @Test
    void givenContestIdAndUser_whenRegisterForContestAndContestStarted_thenThrowsException() {
        // Assign
        long contestId = 1L;
        UserEntity userEntity = new UserEntity(1L, "username", "password", "email");
        ContestEntity contestEntity = ContestEntity.builder()
                .id(1L)
                .title("title")
                .startDateTime(OffsetDateTime.now().minusHours(1))
                .endDateTime(OffsetDateTime.now().plusHours(2))
                .problems(new HashSet<>())
                .participants(new HashSet<>())
                .build();

        Mockito.when(
                userService.getAuthenticatedUser()
        ).thenReturn(
                userEntity
        );

        Mockito.when(
                contestRepository.findById(contestEntity.getId())
        ).thenReturn(
                Optional.of(contestEntity)
        );

        // Act + Assert
        Assertions.assertThrows(
                ContestTimeException.class,
                () -> contestService.registerUserForContest(contestId)
        );
    }

    // Add problem to contest success
    @Test
    void givenContestAndProblem_whenAddProblem_thenAddProblemSuccessfully() {
        // Assign
        long contestId = 1L;
        int problemId = 1;
        UserEntity userEntity = new UserEntity(1L, "username", "password", "email");
        Set<ProblemEntity> problems = Mockito.spy(new HashSet<>());
        ProblemEntity problemEntity = ProblemEntity.builder()
                .id(problemId)
                .title("title")
                .description("description")
                .owner(userEntity)
                .build();
        ContestEntity contestEntity = ContestEntity.builder()
                .id(contestId)
                .title("title")
                .startDateTime(OffsetDateTime.now().plusHours(1))
                .endDateTime(OffsetDateTime.now().plusHours(2))
                .problems(problems)
                .participants(Set.of(userEntity))
                .owner(userEntity)
                .build();

        Mockito.when(
                contestRepository.findById(contestEntity.getId())
        ).thenReturn(
                Optional.of(contestEntity)
        );

        Mockito.when(
                userService.getAuthenticatedUser()
        ).thenReturn(
                userEntity
        );

        Mockito.when(
                problemService.getProblemEntity(problemEntity.getId())
        ).thenReturn(
                problemEntity
        );

        // Act
        contestService.addProblem(contestId, problemId);

        // Assert
        Mockito.verify(problems).add(problemEntity);
        Mockito.verify(contestRepository).save(contestEntity);
    }

    // Add problem to contest failed: problem not found
    @Test
    void givenContestAndProblem_whenAddProblemAndProblemNotFound_thenThrowException() {
        // Assign
        long contestId = 1L;
        int problemId = 1;
        UserEntity userEntity = new UserEntity(1L, "username", "password", "email");
        ProblemEntity problemEntity = ProblemEntity.builder()
                .id(problemId)
                .title("title")
                .description("description")
                .owner(userEntity)
                .build();
        ContestEntity contestEntity = ContestEntity.builder()
                .id(contestId)
                .title("title")
                .startDateTime(OffsetDateTime.now().plusHours(1))
                .endDateTime(OffsetDateTime.now().plusHours(2))
                .problems(Set.of())
                .participants(Set.of(userEntity))
                .owner(userEntity)
                .build();

        Mockito.when(
                contestRepository.findById(contestEntity.getId())
        ).thenReturn(
                Optional.of(contestEntity)
        );

        Mockito.when(
                userService.getAuthenticatedUser()
        ).thenReturn(
                userEntity
        );

        Mockito.when(
                problemService.getProblemEntity(problemEntity.getId())
        ).thenThrow(
                ProblemNotFoundException.class
        );

        // Act + Assert
        Assertions.assertThrows(
                ProblemNotFoundException.class,
                () -> contestService.addProblem(contestId, problemId)
        );
    }

    // Add problem to contest failed: user not owner
    @Test
    void givenContestAndProblem_whenAddProblemAndUserNotOwner_thenThrowsException() {
        // Assign
        long contestId = 1L;
        int problemId = 1;
        UserEntity userEntity = new UserEntity(1L, "username", "password", "email");
        ProblemEntity problemEntity = ProblemEntity.builder()
                .id(problemId)
                .title("title")
                .description("description")
                .build();
        ContestEntity contestEntity = ContestEntity.builder()
                .id(contestId)
                .title("title")
                .startDateTime(OffsetDateTime.now().plusHours(1))
                .endDateTime(OffsetDateTime.now().plusHours(2))
                .problems(Set.of(problemEntity))
                .participants(Set.of(userEntity))
                .owner(new UserEntity(2L, "username", "password", "email"))
                .build();

        Mockito.when(
                contestRepository.findById(contestEntity.getId())
        ).thenReturn(
                Optional.of(contestEntity)
        );

        Mockito.when(
                userService.getAuthenticatedUser()
        ).thenReturn(
                userEntity
        );

        // Act + Assert
        Assertions.assertThrows(
                ForbiddenException.class,
                () -> contestService.addProblem(contestId, problemId)
        );

    }

    // Submit success
    @Test
    void givenSubmission_whenSubmit_thenThrowsException() {
        // Assign
        UserEntity userEntity = new UserEntity(1L, "username", "password", "email");
        ProblemEntity problemEntity = ProblemEntity.builder()
                .id(1)
                .title("title")
                .description("description")
                .owner(userEntity)
                .build();
        ContestEntity contestEntity = ContestEntity.builder()
                .id(1L)
                .title("title")
                .startDateTime(OffsetDateTime.now().minusHours(1))
                .endDateTime(OffsetDateTime.now().plusHours(1))
                .problems(Set.of(problemEntity))
                .participants(Set.of(userEntity))
                .build();
        SubmissionRequest submissionRequest = new SubmissionRequest("source code", "language");

        Mockito.when(
                contestRepository.findById(contestEntity.getId())
        ).thenReturn(
                Optional.of(contestEntity)
        );

        Mockito.when(
                userService.getAuthenticatedUser()
        ).thenReturn(
                userEntity
        );

        Mockito.when(
                problemService.getProblemEntity(problemEntity.getId())
        ).thenReturn(
                problemEntity
        );

        // Act
        contestService.submit(1L, 1L, submissionRequest);

        // Assert
        Mockito.verify(submissionServiceClient).submit(
                new SubmissionMessage(
                        userEntity.getId(),
                        contestEntity.getId(),
                        problemEntity.getId(),
                        submissionRequest.sourceCode(),
                        submissionRequest.language(),
                        Mockito.any()
                )
        );

    }

    // Submit failed: contest not started yet
    @Test
    void givenSubmission_whenSubmitAndContestNotStartedYet_thenThrowsException() {
        // Assign
        UserEntity userEntity = new UserEntity(1L, "username", "password", "email");
        ProblemEntity problemEntity = ProblemEntity.builder()
                .id(1)
                .title("title")
                .description("description")
                .owner(userEntity)
                .build();
        ContestEntity contestEntity = ContestEntity.builder()
                .id(1L)
                .title("title")
                .startDateTime(OffsetDateTime.now().plusHours(1))
                .endDateTime(OffsetDateTime.now().plusHours(2))
                .problems(Set.of(problemEntity))
                .participants(Set.of(userEntity))
                .build();
        SubmissionRequest submissionRequest = new SubmissionRequest("source code", "language");

        Mockito.when(
                contestRepository.findById(contestEntity.getId())
        ).thenReturn(
                Optional.of(contestEntity)
        );

        // Act + Assert
        Assertions.assertThrows(
                ContestTimeException.class,
                () -> contestService.submit(1L, 1L, submissionRequest)
        );
    }

    // Submit failed: contest already finished
    @Test
    void givenSubmission_whenSubmitAndContestAlreadyFinished_thenThrowsException() {
        // Assign
        UserEntity userEntity = new UserEntity(1L, "username", "password", "email");
        ProblemEntity problemEntity = ProblemEntity.builder()
                .id(1)
                .title("title")
                .description("description")
                .owner(userEntity)
                .build();
        ContestEntity contestEntity = ContestEntity.builder()
                .id(1L)
                .title("title")
                .startDateTime(OffsetDateTime.now().minusHours(2))
                .endDateTime(OffsetDateTime.now().minusHours(1))
                .problems(Set.of(problemEntity))
                .participants(Set.of(userEntity))
                .build();
        SubmissionRequest submissionRequest = new SubmissionRequest("source code", "language");

        Mockito.when(
                contestRepository.findById(contestEntity.getId())
        ).thenReturn(
                Optional.of(contestEntity)
        );

        // Act + Assert
        Assertions.assertThrows(
                ContestTimeException.class,
                () -> contestService.submit(1L, 1L, submissionRequest)
        );
    }

    // Submit failed: user not participant
    @Test
    void givenSubmission_whenSubmitAndUserNotParticipant_thenThrowsException() {
        // Assign
        UserEntity userEntity = new UserEntity(1L, "username", "password", "email");
        ProblemEntity problemEntity = ProblemEntity.builder()
                .id(1)
                .title("title")
                .description("description")
                .owner(userEntity)
                .build();
        ContestEntity contestEntity = ContestEntity.builder()
                .id(1L)
                .title("title")
                .startDateTime(OffsetDateTime.now().minusHours(1))
                .endDateTime(OffsetDateTime.now().plusHours(1))
                .problems(Set.of(problemEntity))
                .participants(Set.of())
                .build();
        SubmissionRequest submissionRequest = new SubmissionRequest("source code", "language");

        Mockito.when(
                contestRepository.findById(contestEntity.getId())
        ).thenReturn(
                Optional.of(contestEntity)
        );

        Mockito.when(
                userService.getAuthenticatedUser()
        ).thenReturn(
                userEntity
        );

        // Act + Assert
        Assertions.assertThrows(
                ForbiddenException.class,
                () -> contestService.submit(1L, 1L, submissionRequest)
        );
    }

    // Submit failed: problem not in contest
    @Test
    void givenSubmission_whenSubmitAndProblemNotFoundInContest_thenThrowsException() {
        // Assign
        UserEntity userEntity = new UserEntity(1L, "username", "password", "email");
        ProblemEntity problemEntity = ProblemEntity.builder()
                .id(1)
                .title("title")
                .description("description")
                .owner(userEntity)
                .build();
        ContestEntity contestEntity = ContestEntity.builder()
                .id(1L)
                .title("title")
                .startDateTime(OffsetDateTime.now().minusHours(1))
                .endDateTime(OffsetDateTime.now().plusHours(2))
                .participants(Set.of(userEntity))
                .problems(Set.of())
                .build();
        SubmissionRequest submissionRequest = new SubmissionRequest("source code", "language");

        Mockito.when(
                contestRepository.findById(contestEntity.getId())
        ).thenReturn(
                Optional.of(contestEntity)
        );

        Mockito.when(
                userService.getAuthenticatedUser()
        ).thenReturn(
                userEntity
        );

        Mockito.when(
                problemService.getProblemEntity(problemEntity.getId())
        ).thenReturn(
                problemEntity
        );

        // Act + Assert
        Assertions.assertThrows(
                ProblemNotFoundException.class,
                () -> contestService.submit(1L, 1L, submissionRequest)
        );
    }
}