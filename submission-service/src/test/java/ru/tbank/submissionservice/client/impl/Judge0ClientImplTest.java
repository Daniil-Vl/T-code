package ru.tbank.submissionservice.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.web.reactive.function.client.WebClient;
import ru.tbank.submissionservice.dto.language.Language;
import ru.tbank.submissionservice.dto.submission.SubmissionRequestDTO;
import ru.tbank.submissionservice.dto.submission.SubmissionToken;
import ru.tbank.submissionservice.integration.AbstractIntegrationTest;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;

class Judge0ClientImplTest extends AbstractIntegrationTest {

    @Autowired
    private Judge0ClientImpl client;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    private WebClient webClient;

    @Test
    void givenSubmissionBatch_whenSubmitBatch_thenSuccessfullyGetSubmissionTokensBatch() throws JsonProcessingException {
        // Arrange
        List<SubmissionRequestDTO> submissionRequests = List.of(
                new SubmissionRequestDTO("source code 1", 1, "stdin 1", "expected stdout 1"),
                new SubmissionRequestDTO("source code 2", 2, "stdin 2", "expected stdout 2")
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
        List<Language> actualLanguages = client.getLanguages();

        // Assert
        Assertions.assertIterableEquals(expectedLanguages, actualLanguages);
    }

}