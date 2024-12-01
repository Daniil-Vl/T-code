package ru.tbank.contestservice.integration.contest;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.tbank.contestservice.client.SubmissionServiceClient;
import ru.tbank.contestservice.dao.jpa.ContestRepository;
import ru.tbank.contestservice.dao.jpa.ProblemRepository;
import ru.tbank.contestservice.dto.contest.AddProblemContestRequest;
import ru.tbank.contestservice.dto.contest.ContestDTO;
import ru.tbank.contestservice.dto.contest.CreateContestRequest;
import ru.tbank.contestservice.dto.problem.ProblemDTO;
import ru.tbank.contestservice.integration.AbstractIntegrationTest;
import ru.tbank.contestservice.model.entities.ContestEntity;
import ru.tbank.contestservice.model.entities.ProblemEntity;
import ru.tbank.contestservice.model.entities.UserEntity;
import ru.tbank.contestservice.service.ContestService;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ContestIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/v1/contest";

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private ContestService contestService;

    @MockBean
    private SubmissionServiceClient submissionServiceClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    // Get contest info success
    @Test
    void givenContestId_whenGetContestInfo_thenReturnInfo() throws Exception {
        // Assign
        UserEntity user = newUser("username", "password");
        ContestEntity contest = ContestEntity.builder()
                .title("title")
                .startDateTime(OffsetDateTime.now().minusDays(1))
                .endDateTime(OffsetDateTime.now().plusDays(1))
                .owner(user)
                .build();
        contest = contestRepository.save(contest);

        // Act
        MvcResult mvcResult = mockMvc.perform(
                        get(BASE_URL + "/" + contest.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        ContestDTO contestDTO = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ContestDTO.class
        );

        // Assert
        Assertions.assertEquals(ContestDTO.fromEntity(contest).title(), contestDTO.title());
    }

    // Get contest info failed: contest not found
    @Test
    void givenNonExistentContestId_whenGetContestInfo_thenReturnNotFound() throws Exception {
        // Assign
        UserEntity user = newUser("username", "password");
        ContestEntity contest = ContestEntity.builder()
                .title("title")
                .startDateTime(OffsetDateTime.now().minusDays(1))
                .endDateTime(OffsetDateTime.now().plusDays(1))
                .owner(user)
                .build();
        contest = contestRepository.save(contest);

        // Act + Assert
        mockMvc.perform(
                        get(BASE_URL + "/" + (contest.getId() + 1))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Get contest problems success
    @Test
    void givenContestId_whenGetProblems_thenReturnProblems() throws Exception {
        // Assign
        UserEntity user = newUser("username", "password");
        ProblemEntity problemEntity = ProblemEntity.builder()
                .title("title")
                .owner(user)
                .description("description")
                .build();
        problemEntity = problemRepository.save(problemEntity);
        ContestEntity contest = ContestEntity.builder()
                .title("title")
                .startDateTime(OffsetDateTime.now().minusDays(1))
                .endDateTime(OffsetDateTime.now().plusDays(1))
                .owner(user)
                .problems(Set.of(problemEntity))
                .build();
        contest = contestRepository.save(contest);
        String expectedResponseBody = objectMapper.writeValueAsString(List.of(ProblemDTO.fromEntity(problemEntity)));

        // Act + Assert
        mockMvc.perform(
                        get(BASE_URL + "/" + contest.getId() + "/problems")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseBody));
    }

    // Get contest problems failed: contest not found
    @Test
    void givenNonExistentContestId_whenGetProblems_thenReturnNotFound() throws Exception {
        // Assign
        UserEntity user = newUser("username", "password");
        ProblemEntity problemEntity = ProblemEntity.builder()
                .title("title")
                .owner(user)
                .description("description")
                .build();
        problemEntity = problemRepository.save(problemEntity);
        ContestEntity contest = ContestEntity.builder()
                .title("title")
                .startDateTime(OffsetDateTime.now().minusDays(1))
                .endDateTime(OffsetDateTime.now().plusDays(1))
                .owner(user)
                .problems(Set.of(problemEntity))
                .build();
        contest = contestRepository.save(contest);

        // Act + Assert
        mockMvc.perform(
                        get(BASE_URL + "/" + (contest.getId() + 1) + "/problems")
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Get contest results success
    @Test
    void givenContestId_whenGetResults_thenReturnResults() throws Exception {
        // Assign
        UserEntity user = newUser("username", "password");
        ContestEntity contest = ContestEntity.builder()
                .title("title")
                .startDateTime(OffsetDateTime.now().minusDays(1))
                .endDateTime(OffsetDateTime.now().plusDays(1))
                .owner(user)
                .problems(Set.of())
                .build();
        contest = contestRepository.save(contest);

        // Act
        mockMvc.perform(
                        get(BASE_URL + "/" + contest.getId() + "/results")
                )
                .andDo(print())
                .andExpect(status().isOk());

        // Assert
        Mockito.verify(submissionServiceClient).getContestResult(contest.getId());
    }

    // Get contest results failed: contest not found
    @Test
    void givenNonExistentContestId_whenGetResults_thenNotFound() throws Exception {
        // Assign
        UserEntity user = newUser("username", "password");
        ContestEntity contest = ContestEntity.builder()
                .title("title")
                .startDateTime(OffsetDateTime.now().minusDays(1))
                .endDateTime(OffsetDateTime.now().plusDays(1))
                .owner(user)
                .problems(Set.of())
                .build();
        contest = contestRepository.save(contest);

        // Act + assert
        mockMvc.perform(
                        get(BASE_URL + "/" + (contest.getId() + 1) + "/results")
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Create contest success
    @Test
    void givenCreateContestData_whenCreateContest_thenCreateContestSuccessfully() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        CreateContestRequest request = new CreateContestRequest("title", OffsetDateTime.now(), OffsetDateTime.now(), List.of());
        String requestBody = objectMapper.writeValueAsString(request);

        // Act
        mockMvc.perform(
                        post(BASE_URL)
                                .header("Authorization", authHeaderValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isCreated());

        // Assert
        List<ContestEntity> contests = contestRepository.findAll();
        Assertions.assertEquals(1, contests.size());
        ContestEntity first = contests.getFirst();
        Assertions.assertEquals("title", first.getTitle());
    }

    // Create contest failed: unauthenticated
    @Test
    void givenUnauthenticatedUser_whenCreateContest_thenReturnUnauthorized() throws Exception {
        // Assign
        UserEntity user = newUser("username", "password");
        CreateContestRequest request = new CreateContestRequest("title", OffsetDateTime.now(), OffsetDateTime.now(), List.of());
        String requestBody = objectMapper.writeValueAsString(request);

        // Act
        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // Create contest failed: problem not found
    @Test
    void givenNonExistentProblemid_whenCreateContest_thenReturnNotFound() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        CreateContestRequest request = new CreateContestRequest("title", OffsetDateTime.now(), OffsetDateTime.now(), List.of(1L));
        String requestBody = objectMapper.writeValueAsString(request);

        // Act
        mockMvc.perform(
                        post(BASE_URL)
                                .header("Authorization", authHeaderValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Add problem success
    @Test
    @Transactional
    void givenProblemAndContest_whenAddProblem_thenAddProblemSuccessfully() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ContestEntity contest = ContestEntity.builder()
                .title("title")
                .startDateTime(OffsetDateTime.now().plusDays(1))
                .endDateTime(OffsetDateTime.now().plusDays(2))
                .owner(user)
                .problems(new HashSet<>())
                .build();
        contest = contestRepository.save(contest);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .owner(user)
                .description("description")
                .build();
        problem = problemRepository.save(problem);
        AddProblemContestRequest request = new AddProblemContestRequest(contest.getId(), problem.getId());
        String requestBody = objectMapper.writeValueAsString(request);

        // Act
        mockMvc.perform(
                        put(BASE_URL + "/problem")
                                .header("Authorization", authHeaderValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isOk());

        // Assert
        ContestEntity contestEntity = contestService.getContestEntity(contest.getId());
        Assertions.assertTrue(contestEntity.getProblems().contains(problem));
    }

    // Add problem failed: contest already finished
    @Test
    void givenFinishedContest_whenAddProblem_thenReturnBadRequest() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ContestEntity contest = ContestEntity.builder()
                .title("title")
                .startDateTime(OffsetDateTime.now().minusDays(2))
                .endDateTime(OffsetDateTime.now().minusDays(1))
                .owner(user)
                .problems(Set.of())
                .build();
        contest = contestRepository.save(contest);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .owner(user)
                .description("description")
                .build();
        problem = problemRepository.save(problem);
        AddProblemContestRequest request = new AddProblemContestRequest(contest.getId(), problem.getId());
        String requestBody = objectMapper.writeValueAsString(request);

        // Act
        mockMvc.perform(
                        put(BASE_URL + "/problem")
                                .header("Authorization", authHeaderValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Add problem failed: unauthenticated
    @Test
    void givenUnauthenticatedUser_whenAddProblem_thenReturnUnauthorized() throws Exception {
        // Assign
        UserEntity user = newUser("username", "password");
        ContestEntity contest = ContestEntity.builder()
                .title("title")
                .startDateTime(OffsetDateTime.now().minusDays(1))
                .endDateTime(OffsetDateTime.now().plusDays(1))
                .owner(user)
                .problems(Set.of())
                .build();
        contest = contestRepository.save(contest);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .owner(user)
                .description("description")
                .build();
        problem = problemRepository.save(problem);
        AddProblemContestRequest request = new AddProblemContestRequest(contest.getId(), problem.getId());
        String requestBody = objectMapper.writeValueAsString(request);

        // Act
        mockMvc.perform(
                        put(BASE_URL + "/problem")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // Add problem failed: user not contest`s owner
    @Test
    void givenNonProblemOwner_whenAddProblem_thenReturnForbidden() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ContestEntity contest = ContestEntity.builder()
                .title("title")
                .startDateTime(OffsetDateTime.now().plusDays(1))
                .endDateTime(OffsetDateTime.now().plusDays(2))
                .owner(newUser("user", "pass"))
                .problems(Set.of())
                .build();
        contest = contestRepository.save(contest);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .owner(user)
                .description("description")
                .build();
        problem = problemRepository.save(problem);
        AddProblemContestRequest request = new AddProblemContestRequest(contest.getId(), problem.getId());
        String requestBody = objectMapper.writeValueAsString(request);

        // Act
        mockMvc.perform(
                        put(BASE_URL + "/problem")
                                .header("Authorization", authHeaderValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // Add problem failed: problem not found
    @Test
    void givenNonExistentProblemId_whenAddProblem_thenReturnNotFound() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ContestEntity contest = ContestEntity.builder()
                .title("title")
                .startDateTime(OffsetDateTime.now().plusDays(1))
                .endDateTime(OffsetDateTime.now().plusDays(2))
                .owner(user)
                .problems(Set.of())
                .build();
        contest = contestRepository.save(contest);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .owner(user)
                .description("description")
                .build();
        problem = problemRepository.save(problem);
        AddProblemContestRequest request = new AddProblemContestRequest(contest.getId(), problem.getId() + 1);
        String requestBody = objectMapper.writeValueAsString(request);

        // Act
        mockMvc.perform(
                        put(BASE_URL + "/problem")
                                .header("Authorization", authHeaderValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Add problem failed: contest not found
    @Test
    void givenNonExistentContestId_whenAddProblem_thenReturnNotFound() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ContestEntity contest = ContestEntity.builder()
                .title("title")
                .startDateTime(OffsetDateTime.now().minusDays(1))
                .endDateTime(OffsetDateTime.now().plusDays(1))
                .owner(user)
                .problems(Set.of())
                .build();
        contest = contestRepository.save(contest);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .owner(user)
                .description("description")
                .build();
        problem = problemRepository.save(problem);
        AddProblemContestRequest request = new AddProblemContestRequest(contest.getId() + 1, problem.getId());
        String requestBody = objectMapper.writeValueAsString(request);

        // Act
        mockMvc.perform(
                        put(BASE_URL + "/problem")
                                .header("Authorization", authHeaderValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Register for contest success
    @Test
    @Transactional
    void givenContestId_whenRegisterForContest_thenRegisteredSuccessfully() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ContestEntity contest = ContestEntity.builder()
                .title("title")
                .startDateTime(OffsetDateTime.now().plusDays(1))
                .endDateTime(OffsetDateTime.now().plusDays(2))
                .owner(user)
                .problems(Set.of())
                .participants(new HashSet<>())
                .build();
        contest = contestRepository.save(contest);

        // Act
        mockMvc.perform(
                        post(BASE_URL + "/" + contest.getId() + "/register")
                                .header("Authorization", authHeaderValue)
                )
                .andDo(print())
                .andExpect(status().isOk());

        // Assert
        ContestEntity contestEntity = contestService.getContestEntity(contest.getId());
        Assertions.assertTrue(contestEntity.getParticipants().contains(user));
    }

    // Register for contest failed: registration already finished
    @Test
    void givenRegistrationAlreadyFinished_whenRegisterForContest_thenReturnBadRequest() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ContestEntity contest = ContestEntity.builder()
                .title("title")
                .startDateTime(OffsetDateTime.now().minusDays(1))
                .endDateTime(OffsetDateTime.now().plusDays(1))
                .owner(user)
                .problems(Set.of())
                .build();
        contest = contestRepository.save(contest);

        // Act
        mockMvc.perform(
                        post(BASE_URL + "/" + contest.getId() + "/register")
                                .header("Authorization", authHeaderValue)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Register for contest failed: unauthenticated
    @Test
    void givenUnauthenticatedUser_whenRegisterForContest_thenReturnUnauthorized() throws Exception {
        // Assign
        UserEntity user = newUser("username", "password");
        ContestEntity contest = ContestEntity.builder()
                .title("title")
                .startDateTime(OffsetDateTime.now().plusDays(1))
                .endDateTime(OffsetDateTime.now().plusDays(2))
                .owner(user)
                .problems(Set.of())
                .build();
        contest = contestRepository.save(contest);

        // Act
        mockMvc.perform(
                        post(BASE_URL + "/" + contest.getId() + "/register")
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // Register for contest failed: contest not found
    @Test
    void givenNonExistentContest_whenRegisterForContest_thenReturnNotFound() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ContestEntity contest = ContestEntity.builder()
                .title("title")
                .startDateTime(OffsetDateTime.now().plusDays(1))
                .endDateTime(OffsetDateTime.now().plusDays(2))
                .owner(user)
                .problems(Set.of())
                .build();
        contest = contestRepository.save(contest);

        // Act
        mockMvc.perform(
                        post(BASE_URL + "/" + (contest.getId() + 1) + "/register")
                                .header("Authorization", authHeaderValue)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
