package ru.tbank.submissionservice.integration.contest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.tbank.submissionservice.dao.jpa.SubmissionRepository;
import ru.tbank.submissionservice.dto.contest.ContestResult;
import ru.tbank.submissionservice.dto.contest.UserRating;
import ru.tbank.submissionservice.enums.SubmissionStatus;
import ru.tbank.submissionservice.integration.AbstractIntegrationTest;
import ru.tbank.submissionservice.model.entities.SubmissionEntity;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ContestIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/v1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SubmissionRepository submissionRepository;

    // Get contest result success
    @Test
    void givenContestId_whenGetContestResult_thenReturnResultsSuccessfully() throws Exception {
        // Assign
        long contestId = 1;
        long firstUserId = 2;
        long problemId = 3;
        long secondUserId = 4;
        var firstSubmission = SubmissionEntity.builder()
                .contestId(contestId)
                .userId(firstUserId)
                .problemId(problemId)
                .status(SubmissionStatus.WRONG_ANSWER.getValue())
                .submittedAt(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .score(8)
                .executionTimeMs(10)
                .memoryUsedKb(10)
                .language("python")
                .build();
        var secondSubmission = SubmissionEntity.builder()
                .contestId(contestId)
                .userId(secondUserId)
                .problemId(problemId)
                .submittedAt(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .status(SubmissionStatus.ACCEPTED.getValue())
                .score(10)
                .executionTimeMs(15)
                .memoryUsedKb(15)
                .language("java")
                .build();
        submissionRepository.save(firstSubmission);
        submissionRepository.save(secondSubmission);
        ContestResult expectedContestResult = new ContestResult(
                contestId,
                List.of(
                        ContestResult.ProblemResult.fromEntity(firstSubmission),
                        ContestResult.ProblemResult.fromEntity(secondSubmission)
                )
        );

        // Act
        MvcResult mvcResult = mockMvc.perform(
                        get(BASE_URL + "/contest/{contest_id}/result", contestId)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        ContestResult actualContestResult = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        // Assert
        System.out.println("Expected contest result: " + expectedContestResult);
        System.out.println("Actual contest result: " + actualContestResult);
        Assertions.assertEquals(expectedContestResult, actualContestResult);
    }

    // Get contest result success: if contest id not found, then return empty list
    @Test
    void givenNonExistentContestId_whenGetContestResult_thenReturnEmptyList() throws Exception {
        // Assign
        long contestId = 10;

        // Act
        MvcResult mvcResult = mockMvc.perform(
                        get(BASE_URL + "/contest/{contest_id}/result", contestId)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        ContestResult actualContestResult = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        // Assert
        Assertions.assertEquals(contestId, actualContestResult.contestId());
        Assertions.assertEquals(0, actualContestResult.problemResults().size());
    }

    // Get user rating success
    @Test
    void givenContestId_whenGetUserRating_thenReturnRatingSuccessfully() throws Exception {
        // Assign
        long contestId = 1;
        long firstUserId = 2;
        long problemId = 3;
        long secondUserId = 4;
        var firstSubmission = SubmissionEntity.builder()
                .contestId(contestId)
                .userId(firstUserId)
                .problemId(problemId)
                .status(SubmissionStatus.WRONG_ANSWER.getValue())
                .submittedAt(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .score(8)
                .executionTimeMs(10)
                .memoryUsedKb(10)
                .language("python")
                .build();
        var secondSubmission = SubmissionEntity.builder()
                .contestId(contestId)
                .userId(secondUserId)
                .problemId(problemId)
                .status(SubmissionStatus.ACCEPTED.getValue())
                .submittedAt(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .score(10)
                .executionTimeMs(15)
                .memoryUsedKb(15)
                .language("java")
                .build();
        submissionRepository.save(firstSubmission);
        submissionRepository.save(secondSubmission);
        UserRating expectedUserRating = new UserRating(
                contestId,
                List.of(
                        new UserRating.User(secondUserId, 1, 10),
                        new UserRating.User(firstUserId, 0, 8)
                )
        );


        // Act
        MvcResult mvcResult = mockMvc.perform(
                        get(BASE_URL + "/contest/{contest_id}/rating", contestId)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        UserRating actualUserRating = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        // Assert
        Assertions.assertEquals(expectedUserRating, actualUserRating);
    }

    // Get user rating success: : if contest id not found, then return empty list
    @Test
    void givenNonExistentContestId_whenGetUserRating_thenReturnEmptyList() throws Exception {
        // Assign
        long contestId = 10;

        // Act
        MvcResult mvcResult = mockMvc.perform(
                        get(BASE_URL + "/contest/{contest_id}/rating", contestId)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        UserRating actualUserRating = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        // Assert
        Assertions.assertEquals(contestId, actualUserRating.contestId());
        Assertions.assertEquals(0, actualUserRating.userRating().size());
    }
}
