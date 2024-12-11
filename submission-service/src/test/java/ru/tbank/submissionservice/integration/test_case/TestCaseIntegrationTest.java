package ru.tbank.submissionservice.integration.test_case;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.tbank.submissionservice.dto.test_case.TestCase;
import ru.tbank.submissionservice.integration.AbstractIntegrationTest;
import ru.tbank.submissionservice.service.test_case.TestCaseService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestCaseIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/v1/test-case";

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestCaseService testCaseService;

    @BeforeEach
    void setUp() {
        createBuckets();
    }

    @AfterEach
    void tearDown() {
        deleteBuckets();
    }

    // Test cases successfully retrieved
    @Test
    void givenProblemId_whenGetTestCases_thenRetrieveSuccessfully() throws Exception {
        // Assign
        long problemId = 1;
        List<TestCase> expectedTestCases = List.of(
                new TestCase("first input", "first output"),
                new TestCase("second input", "second output")
        );
        testCaseService.saveTestCases(problemId, expectedTestCases);

        // Act
        MvcResult mvcResult = mockMvc.perform(
                        get(BASE_URL + "/problem/{problemId}", problemId)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<TestCase> actualTestCases = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        // Assert
        Assertions.assertIterableEquals(expectedTestCases, actualTestCases);
    }

    // Problem not found
    @Test
    void givenNonExistentProblemId_whenGetTestCases_thenReturnNotFound() throws Exception {
        // Assign
        long problemId = 1;

        // Act + Assert
        mockMvc.perform(
                        get(BASE_URL + "/problem/{problemId}", problemId)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Save test cases success
    @Test
    void givenProblemId_whenSaveTestCases_thenTestCasesSuccessfullySaved() throws Exception {
        // Assign
        long problemId = 1;
        List<TestCase> expectedTestCases = List.of(
                new TestCase("first input", "first output"),
                new TestCase("second input", "second output")
        );
        String requestBody = objectMapper.writeValueAsString(expectedTestCases);

        // Act
        mockMvc.perform(
                        post(BASE_URL + "/problem/{problemId}", problemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isOk());

        // Assert
        List<TestCase> actualTestCases = testCaseService.getTestCases(problemId);
        Assertions.assertIterableEquals(expectedTestCases, actualTestCases);
    }

    // Save test cases failed: invalid test cases
    @Test
    void givenInvalidTestCases_whenSaveTestCases_thenReturnBadRequest() throws Exception {
        // Assign
        long problemId = 1;
        List<TestCase> expectedTestCases = List.of(
                new TestCase(null, "first output"),
                new TestCase("second input", "second output")
        );
        String requestBody = objectMapper.writeValueAsString(expectedTestCases);

        // Act + Assert
        mockMvc.perform(
                        post(BASE_URL + "/problem/{problemId}", problemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
