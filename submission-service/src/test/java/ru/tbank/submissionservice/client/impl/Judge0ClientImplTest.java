package ru.tbank.submissionservice.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.spring.EnableWireMock;
import ru.tbank.submissionservice.PostgreSQLIntegration;
import ru.tbank.submissionservice.dto.SubmissionResult;
import ru.tbank.submissionservice.dto.SubmissionToken;
import ru.tbank.submissionservice.enums.Language;
import ru.tbank.submissionservice.exception.ServiceException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

// TODO: Finish this test
@SpringBootTest(properties = "app.judge0-api-base-url=${wiremock.server.baseUrl}")
@EnableWireMock
@Testcontainers
class Judge0ClientImplTest extends PostgreSQLIntegration {

    @Autowired
    private Judge0ClientImpl client;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenSubmission_whenSubmit_thenSuccessfullyGetSubmissionToken() throws JsonProcessingException {
        // Arrange
        String sourceCode = "source code";
        Language language = Language.JAVA;
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
        SubmissionToken submissionToken = client.submit(sourceCode, language, stdin);

        // Assert
        Assertions.assertNotNull(submissionToken.token());
    }

    @Test
    void givenFullQueueInJudge0_whenSubmit_thenRetry3TimesAndThrowServiceException() throws JsonProcessingException {
        // Arrange
        String sourceCode = "source code";
        Language language = Language.JAVA;
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
                () -> client.submit(sourceCode, language, stdin)
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

    // TODO: Test for submit waiting
    // TODO: Test for submit batch

    private record Judge0Error(String error) {
    }

}