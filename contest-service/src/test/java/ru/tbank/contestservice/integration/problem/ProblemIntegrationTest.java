package ru.tbank.contestservice.integration.problem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.tbank.contestservice.client.SubmissionServiceClient;
import ru.tbank.contestservice.dao.jpa.ProblemRepository;
import ru.tbank.contestservice.dao.jpa.TestCaseRepository;
import ru.tbank.contestservice.dao.jpa.UserRepository;
import ru.tbank.contestservice.dto.TestCase;
import ru.tbank.contestservice.dto.problem.ProblemDTO;
import ru.tbank.contestservice.integration.AbstractIntegrationTest;
import ru.tbank.contestservice.model.entities.ProblemEntity;
import ru.tbank.contestservice.model.entities.UserEntity;
import ru.tbank.contestservice.service.ProblemService;
import ru.tbank.contestservice.service.UserService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProblemIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/v1/problem";
    private static final String TEST_CASE_PATH = "/api/v1/test-case/problem";

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private UserRepository userRepository;

    @SpyBean
    private TestCaseRepository testCaseRepository;

    @Autowired
    private SubmissionServiceClient submissionServiceClient;

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Create problem success
    @Test
    void givenProblemDTO_whenCreateProblem_thenProblemCreated() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ProblemDTO problemDTO = new ProblemDTO(1, "title", "description", List.of(new TestCase("input", "output")));
        String requestBody = objectMapper.writeValueAsString(problemDTO);
        WireMock.stubFor(
                WireMock.post(WireMock.urlPathMatching(TEST_CASE_PATH + "/\\d+"))
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(200)
                        )
        );

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
        List<ProblemEntity> problems = problemRepository.findAll();
        Assertions.assertEquals(1, problems.size());
        ProblemEntity problem = problems.getFirst();
        Assertions.assertEquals("title", problem.getTitle());
        Assertions.assertEquals("description", problem.getDescription());
        Assertions.assertEquals(userService.loadUserByUsername(username), problem.getOwner());
    }

    // Create problem failed: blank problem title
    @Test
    void givenBlankProblemTitle_whenCreateProblem_thenReturnBadRequest() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ProblemDTO problemDTO = new ProblemDTO(1, "", "description", List.of(new TestCase("input", "output")));
        String requestBody = objectMapper.writeValueAsString(problemDTO);
        WireMock.stubFor(
                WireMock.post(WireMock.urlPathMatching(TEST_CASE_PATH + "/\\d+"))
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(200)
                        )
        );

        // Act
        mockMvc.perform(
                        post(BASE_URL)
                                .header("Authorization", authHeaderValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Assert
        Assertions.assertTrue(problemRepository.findAll().isEmpty());
    }

    // Create problem failed: unauthenticated
    @Test
    void givenUnauthenticatedUser_whenCreateProblem_thenReturnUnauthorized() throws Exception {
        // Assign
        ProblemDTO problemDTO = new ProblemDTO(1, "title", "description", List.of(new TestCase("input", "output")));
        String requestBody = objectMapper.writeValueAsString(problemDTO);

        // Act
        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // Assert
        Assertions.assertTrue(problemRepository.findAll().isEmpty());
    }

    // Get problem by id success
    @Test
    void givenProblemId_whenGetProblemById_thenReturnProblem() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .description("description")
                .owner(user)
                .build();
        problem = problemRepository.save(problem);
        WireMock.stubFor(
                WireMock.get(WireMock.urlPathMatching(TEST_CASE_PATH + "/\\d+"))
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(200)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .withBody("[]")
                        )
        );

        // Act
        MvcResult mvcResult = mockMvc.perform(
                        get(BASE_URL + "/" + problem.getId())
                                .header("Authorization", authHeaderValue)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        ProblemDTO actualProblemDTO = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ProblemDTO.class
        );

        // Assert
        Assertions.assertEquals(ProblemDTO.fromEntity(problem), actualProblemDTO);
    }

    // Get problem by id failed: not positive problem id
    @Test
    void givenNegativeProblemId_whenGetProblemById_thenReturnBadRequest() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .description("description")
                .owner(user)
                .build();
        problemRepository.save(problem);

        // Act
        mockMvc.perform(
                        get(BASE_URL + "/" + -1 * problem.getId())
                                .header("Authorization", authHeaderValue)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    // Get problem by id failed: unauthenticated
    @Test
    void givenUnauthenticatedUser_whenGetProblemById_thenReturnUnauthorized() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        setAuth(username, password);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .description("description")
                .owner(user)
                .build();
        problemRepository.save(problem);

        // Act + Assert
        mockMvc.perform(
                        get(BASE_URL + "/" + problem.getId())
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // Get problem by id failed: user not problem owner
    @Test
    void givenUserNotProblemOwner_whenGetProblemById_thenReturnForbidden() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        UserEntity otherUser = newUser(username + "!", password + "!");
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .description("description")
                .owner(otherUser)
                .build();
        problemRepository.save(problem);

        // Act
        mockMvc.perform(
                        get(BASE_URL + "/" + problem.getId())
                                .header("Authorization", authHeaderValue)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // Get problem by id failed: problem not found
    @Test
    void givenNonExistentProblemId_whenGetProblemById_thenReturnNotFound() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .description("description")
                .owner(user)
                .build();
        problemRepository.save(problem);

        // Act
        mockMvc.perform(
                        get(BASE_URL + "/" + (problem.getId() + 1))
                                .header("Authorization", authHeaderValue)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Get problem description by id success
    @Test
    void givenProblemId_whenGetProblemDescriptionById_thenReturnDescription() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .description("description")
                .owner(user)
                .build();
        problemRepository.save(problem);

        // Act
        MvcResult mvcResult = mockMvc.perform(
                        get(BASE_URL + "/" + problem.getId() + "/description")
                                .header("Authorization", authHeaderValue)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        ProblemDTO actualProblemDTO = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ProblemDTO.class
        );

        // Assert
        Assertions.assertEquals(ProblemDTO.fromEntity(problem), actualProblemDTO);
    }

    // Get problem description by id failed: not positive problem id
    @Test
    void givenNegativeProblemId_whenGetProblemDescriptionById_thenReturnBadRequest() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .description("description")
                .owner(user)
                .build();
        problemRepository.save(problem);

        // Act
        mockMvc.perform(
                        get(BASE_URL + "/" + -1 * problem.getId() + "/description")
                                .header("Authorization", authHeaderValue)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Get problem description by id failed: problem not found
    @Test
    void givenNonExistentProblemId_whenGetProblemDescription_thenReturnNotFound() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .description("description")
                .owner(user)
                .build();
        problemRepository.save(problem);

        // Act
        mockMvc.perform(
                        get(BASE_URL + "/" + (problem.getId() + 1) + "/description")
                                .header("Authorization", authHeaderValue)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Update problem description success
    @Test
    void givenNewDescription_whenUpdateProblemDescription_thenUpdateSuccessfully() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .description("description")
                .owner(user)
                .build();
        problemRepository.save(problem);
        String newDescription = "new description";

        // Act
        mockMvc.perform(
                        patch(BASE_URL + "/" + problem.getId() + "/description")
                                .header("Authorization", authHeaderValue)
                                .contentType(MediaType.TEXT_PLAIN)
                                .content(newDescription)
                )
                .andDo(print())
                .andExpect(status().isOk());

        // Assert
        ProblemEntity problemEntity = problemService.getProblemEntity(problem.getId());
        Assertions.assertEquals(newDescription, problemEntity.getDescription());
    }

    // Update problem description failed: invalid problem data (description blank)
    @Test
    void givenInvalidDescription_whenUpdateDescription_thenReturnBadRequest() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .description("description")
                .owner(user)
                .build();
        problemRepository.save(problem);
        String newDescription = "  ";

        // Act
        mockMvc.perform(
                        patch(BASE_URL + "/" + problem.getId() + "/description")
                                .header("Authorization", authHeaderValue)
                                .contentType(MediaType.TEXT_PLAIN)
                                .content(newDescription)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Update problem description failed: unauthenticated
    @Test
    void givenDescription_whenUpdateDescriptionAndUserUnauthenticated_thenReturnUnauthorized() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        setAuth(username, password);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .description("description")
                .owner(user)
                .build();
        problemRepository.save(problem);

        // Act + Assert
        mockMvc.perform(
                        patch(BASE_URL + "/" + problem.getId() + "/description")
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // Update problem description failed: user not problem owner
    @Test
    void givenUserNotProblemOwner_whenUpdateDescription_thenReturnForbidden() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        UserEntity otherUser = newUser(username + "!", password + "!");
        otherUser = userRepository.save(otherUser);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .description("description")
                .owner(otherUser)
                .build();
        problemRepository.save(problem);
        String newDescription = "new description";

        // Act
        mockMvc.perform(
                        patch(BASE_URL + "/" + problem.getId() + "/description")
                                .header("Authorization", authHeaderValue)
                                .contentType(MediaType.TEXT_PLAIN)
                                .content(newDescription)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // Update problem description failed: problem not found
    @Test
    void givenNonExistentProblemId_whenUpdateDescription_thenReturnNotFound() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .description("description")
                .owner(user)
                .build();
        problemRepository.save(problem);
        String newDescription = "new description";

        // Act
        mockMvc.perform(
                        patch(BASE_URL + "/" + (problem.getId() + 1) + "/description")
                                .header("Authorization", authHeaderValue)
                                .contentType(MediaType.TEXT_PLAIN)
                                .content(newDescription)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Update problem test cases success
    @Test
    void givenProblem_whenUpdateTestCases_thenUpdateSuccessfully() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .description("description")
                .owner(user)
                .build();
        problemRepository.save(problem);
        List<TestCase> testCases = List.of(new TestCase("input", "output"));
        String requestBody = objectMapper.writeValueAsString(testCases);
        WireMock.stubFor(
                WireMock.post(WireMock.urlPathMatching(TEST_CASE_PATH + "/\\d+"))
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(200)
                        )
        );

        // Act
        mockMvc.perform(
                        put(BASE_URL + "/" + problem.getId() + "/test_cases")
                                .header("Authorization", authHeaderValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isOk());

        // Assert
        Mockito.verify(testCaseRepository).removeAllByProblem(problem);
    }

    // Update problem test cases failed: unauthenticated
    @Test
    void given_when_then() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        setAuth(username, password);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .description("description")
                .owner(user)
                .build();
        problemRepository.save(problem);
        List<TestCase> testCases = List.of(new TestCase("input", "output"));
        String requestBody = objectMapper.writeValueAsString(testCases);

        // Act
        mockMvc.perform(
                        put(BASE_URL + "/" + problem.getId() + "/test_cases")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // Update problem test cases failed: user not problem owner
    @Test
    void givenUserNotProblemOwner_whenUpdateTestCases_thenReturnForbidden() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        UserEntity otherUser = newUser(username + "!", password + "1");
        otherUser = userRepository.save(otherUser);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .description("description")
                .owner(otherUser)
                .build();
        problemRepository.save(problem);
        List<TestCase> testCases = List.of(new TestCase("input", "output"));
        String requestBody = objectMapper.writeValueAsString(testCases);

        // Act
        mockMvc.perform(
                        put(BASE_URL + "/" + problem.getId() + "/test_cases")
                                .header("Authorization", authHeaderValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // Update problem test cases failed: problem not found
    @Test
    void givenNonExistentProblemid_whenUpdateTestCases_thenReturnNotFound() throws Exception {
        // Assign
        String username = "username";
        String password = "password";
        UserEntity user = newUser(username, password);
        String authHeaderValue = setAuth(username, password);
        ProblemEntity problem = ProblemEntity.builder()
                .title("title")
                .description("description")
                .owner(user)
                .build();
        problemRepository.save(problem);
        List<TestCase> testCases = List.of(new TestCase("input", "output"));
        String requestBody = objectMapper.writeValueAsString(testCases);

        // Act
        mockMvc.perform(
                        put(BASE_URL + "/" + (problem.getId() + 1) + "/test_cases")
                                .header("Authorization", authHeaderValue)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
