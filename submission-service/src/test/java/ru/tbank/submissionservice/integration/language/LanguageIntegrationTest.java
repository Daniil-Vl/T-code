package ru.tbank.submissionservice.integration.language;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.tbank.submissionservice.dto.language.Language;
import ru.tbank.submissionservice.integration.AbstractIntegrationTest;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LanguageIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/v1/languages";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void given_whenGetLanguages_thenReturnLanguagesSuccessfully() throws Exception {
        // Assign
        List<Language> expectedLanguages = List.of(
                new Language(1, "Java"),
                new Language(2, "C++"),
                new Language(3, "Python")
        );
        String requestBody = objectMapper.writeValueAsString(expectedLanguages);

        WireMock.stubFor(
                WireMock.get("/languages")
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(200)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(requestBody)
                        )
        );

        // Act
        MvcResult mvcResult = mockMvc.perform(
                        get(BASE_URL)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<String> actualLanguages = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        // Assert
        Assertions.assertIterableEquals(
                expectedLanguages.stream().map(Language::name).toList(),
                actualLanguages
        );
    }

}
