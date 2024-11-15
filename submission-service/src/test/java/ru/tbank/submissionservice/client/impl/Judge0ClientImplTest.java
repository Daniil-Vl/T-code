package ru.tbank.submissionservice.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.spring.EnableWireMock;
import ru.tbank.submissionservice.PostgreSQLIntegration;
import ru.tbank.submissionservice.dto.SubmissionRequestDTO;
import ru.tbank.submissionservice.dto.SubmissionResult;
import ru.tbank.submissionservice.dto.SubmissionToken;
import ru.tbank.submissionservice.enums.Language;
import ru.tbank.submissionservice.exception.ServiceException;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.times;

@SpringBootTest(properties = "app.judge0-api-base-url=${wiremock.server.baseUrl}")
@EnableWireMock
@Testcontainers
class Judge0ClientImplTest extends PostgreSQLIntegration {

    @Autowired
    private Judge0ClientImpl client;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    private WebClient webClient;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void givenSubmission_whenSubmit_thenSuccessfullyGetSubmissionToken() throws JsonProcessingException {
        // Arrange
        String sourceCode = "source code";
        int languageId = 1;
        String stdin = "stdin";

        SubmissionToken token = new SubmissionToken("token");
        String requestBody = objectMapper.writeValueAsString(token);

        stubFor(
                post("/submissions?base64_encoded=false&wait=false")
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(requestBody)
                        )
        );

        // Act
        SubmissionToken submissionToken = client.submit(sourceCode, languageId, stdin);

        // Assert
        Assertions.assertNotNull(submissionToken.token());
    }

    @Test
    void givenFullQueueInJudge0_whenSubmit_thenRetry3TimesAndThrowServiceException() throws JsonProcessingException {
        // Arrange
        String sourceCode = "source code";
        int languageId = 1;
        String stdin = "stdin";

        String requestBody = objectMapper.writeValueAsString(new Judge0Error("queue is full"));

        String testUrl = "/submissions?base64_encoded=false&wait=false";
        stubFor(
                post(testUrl)
                        .willReturn(
                                aResponse()
                                        .withStatus(503)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(requestBody)
                        )
        );

        // Act + Assert
        Assertions.assertThrows(
                ServiceException.class,
                () -> client.submit(sourceCode, languageId, stdin)
        );
        WireMock.verify(4, postRequestedFor(urlEqualTo(testUrl)));
    }

    @Test
    void givenToken_whenGetSubmissionResult_thenSuccessfullyRetrievedResult() throws JsonProcessingException {
        // Arrange
        String token = "token";

        SubmissionResult submissionResult = new SubmissionResult(
                "Hello world",
                1.0,
                1.0,
                "stderr",
                "token",
                "compile output",
                "message",
                new SubmissionResult.Status(1, "accepted")
        );
        String requestBody = objectMapper.writeValueAsString(submissionResult);

        stubFor(
                get("/submissions/" + token)
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(requestBody)
                        )
        );

        // Act
        SubmissionResult actualSubmissionResult = client.getSubmissionResult(token);

        // Assert
        Assertions.assertEquals(submissionResult, actualSubmissionResult);
    }

    @Test
    void givenSubmission_whenSubmitWaiting_thenSuccessfullyGetSubmissionResult() throws JsonProcessingException {
        // Arrange
        String sourceCode = "source code";
        int languageId = 1;
        String stdin = "stdin";

        SubmissionResult expectedSubmissionResult = new SubmissionResult(
                "Hello world",
                1.0,
                1.0,
                "stderr",
                "token",
                "compile output",
                "message",
                new SubmissionResult.Status(1, "accepted")
        );
        String requestBody = objectMapper.writeValueAsString(expectedSubmissionResult);

        stubFor(
                post("/submissions?base64_encoded=false&wait=true")
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(requestBody)
                        )
        );

        // Act
        SubmissionResult actualSubmissionResult = client.submitWaiting(sourceCode, languageId, stdin).block();

        // Assert
        Assertions.assertEquals(expectedSubmissionResult, actualSubmissionResult);
    }

    @Test
    void givenSubmissionBatch_whenSubmitBatch_thenSuccessfullyGetSubmissionTokensBatch() throws JsonProcessingException {
        // Arrange
        List<SubmissionRequestDTO> submissionRequests = List.of(
                new SubmissionRequestDTO("source code 1", 1, "stdin 1"),
                new SubmissionRequestDTO("source code 2", 2, "stdin 2")
        );
        List<SubmissionToken> expectedTokens = List.of(
                new SubmissionToken("first token"),
                new SubmissionToken("second token")
        );
        String requestBody = objectMapper.writeValueAsString(expectedTokens);

        stubFor(
                post("/submissions/batch?base64_encoded=false")
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(requestBody)
                        )
        );

        // Act
        List<SubmissionToken> actualTokens = client.submitBatch(submissionRequests);

        // Assert
        Assertions.assertIterableEquals(expectedTokens, actualTokens);
    }

    @Test
    void given_whenGetLanguages_thenSuccessfullyRetrievedLanguages() throws JsonProcessingException {
        // Arrange
        List<Language> languages = List.of(
                new Language(1, "java (openjdk 21)"),
                new Language(2, "python (python 3.8.1)"),
                new Language(3, "cpp (gcc 8.1.0)"),
                new Language(4, "Common Lisp (SBCL 2.0.0)")
        );
        List<Language> expectedLanguages = List.of(
                new Language(1, "java"),
                new Language(2, "python"),
                new Language(3, "cpp"),
                new Language(4, "common lisp")
        );
        String requestBody = objectMapper.writeValueAsString(languages);


        stubFor(
                get("/languages")
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(requestBody)
                        )
        );

        // Act
        List<Language> actualLanguages = client.getLanguages();

        // Assert
        Assertions.assertIterableEquals(expectedLanguages, actualLanguages);
    }

    @Test
    void given_whenGetLanguagesTwoTimes_thenRequestLanguagesAndSecondGetFromCache() throws JsonProcessingException {
        // Arrange
        cacheManager.getCache("languages-cache").clear();

        List<Language> languages = List.of(
                new Language(1, "java (openjdk 21)"),
                new Language(2, "python (python 3.8.1)"),
                new Language(3, "cpp (gcc 8.1.0)"),
                new Language(4, "Common Lisp (SBCL 2.0.0)")
        );
        String requestBody = objectMapper.writeValueAsString(languages);

        stubFor(
                get("/languages")
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(requestBody)
                        )
        );

        // Act
        client.getLanguages();
        client.getLanguages();


        // Assert
        Mockito.verify(webClient, times(1)).get();
    }

    private record Judge0Error(String error) {
    }

}