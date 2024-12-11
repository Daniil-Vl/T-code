package ru.tbank.submissionservice.integration.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.tbank.submissionservice.client.Judge0Client;
import ru.tbank.submissionservice.dao.jpa.SubmissionRepository;
import ru.tbank.submissionservice.dto.language.Language;
import ru.tbank.submissionservice.dto.submission.SubmissionMessage;
import ru.tbank.submissionservice.dto.submission.SubmissionResult;
import ru.tbank.submissionservice.dto.submission.SubmissionResults;
import ru.tbank.submissionservice.dto.submission.SubmissionToken;
import ru.tbank.submissionservice.dto.test_case.TestCase;
import ru.tbank.submissionservice.enums.SubmissionStatus;
import ru.tbank.submissionservice.integration.AbstractIntegrationTest;
import ru.tbank.submissionservice.model.entities.SubmissionEntity;
import ru.tbank.submissionservice.model.entities.id.SubmissionId;
import ru.tbank.submissionservice.service.source_code.SourceCodeService;
import ru.tbank.submissionservice.service.submission.SubmissionService;
import ru.tbank.submissionservice.service.test_case.TestCaseService;

import java.time.OffsetDateTime;
import java.util.List;


public class RabbitIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${app.rabbit-mq.submission-queue-name}")
    private String submissionQueueName;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    private SourceCodeService sourceCodeService;

    @SpyBean
    private SubmissionService submissionService;

    @SpyBean
    private DLQListener dlqListener;

    @SpyBean
    private SubmissionRepository submissionRepository;

    @Autowired
    private Judge0Client judge0Client;


    @BeforeEach
    void setUp() {
        createBuckets();
    }

    @AfterEach
    void tearDown() {
        deleteBuckets();
    }

    // Submit message success
    @Test
    void givenValidSubmissionMessage_whenSubmitMessage_thenSubmitBatchAndFetchResults() throws Exception {
        // Assign
        long userId = 1;
        long contestId = 2;
        long problemId = 3;
        SubmissionId submissionId = new SubmissionId(userId, contestId, problemId);
        String sourceCode = "print('Hello world')";
        String language = "Python";
        OffsetDateTime submittedAt = OffsetDateTime.now();
        SubmissionMessage submissionMessage = new SubmissionMessage(userId, contestId, problemId, sourceCode, language, submittedAt);
        List<TestCase> testCases = List.of(
                new TestCase("world", "Hello world"),
                new TestCase("hell", "Hello hell")
        );
        testCaseService.saveTestCases(
                problemId,
                testCases
        );
        List<Language> expectedLanguages = List.of(
                new Language(1, "Java"),
                new Language(2, "C++"),
                new Language(3, "Python")
        );
        String languagesRequestBody = objectMapper.writeValueAsString(expectedLanguages);
        List<SubmissionToken> tokens = List.of(
                new SubmissionToken("first_token"),
                new SubmissionToken("second_token")
        );
        String tokensResponseBody = objectMapper.writeValueAsString(tokens);
        SubmissionResults submissionResults = new SubmissionResults(List.of(
                new SubmissionResult("Hello world", 1.0, 1.0, null, "first_token", "", "", new SubmissionResult.Status(2, SubmissionStatus.ACCEPTED.getValue())),
                new SubmissionResult("Hello world", 1.0, 1.0, null, "second_token", "", "", new SubmissionResult.Status(2, SubmissionStatus.WRONG_ANSWER.getValue()))
        ));
        String submissionResultsResponseBody = objectMapper.writeValueAsString(submissionResults);

        WireMock.stubFor(
                WireMock.get("/languages")
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(200)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(languagesRequestBody)
                        )
        );
        WireMock.stubFor(
                WireMock.post("/submissions/batch?base64_encoded=false")
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(tokensResponseBody)
                        )
        );
        WireMock.stubFor(
                WireMock.get("/submissions/batch?base64_encoded=false&tokens="
                                + String.join(",", tokens.stream().map(SubmissionToken::token).toList()))
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(submissionResultsResponseBody)
                        )
        );

        // Act
        rabbitTemplate.convertAndSend(submissionQueueName, submissionMessage);

        // Assert
        // Verify, that fetching task started and finished
        Mockito.verify(submissionService, Mockito.timeout(10000).times(1)).getBatchSubmissionResult(Mockito.anyList());
        Mockito.verify(submissionRepository, Mockito.timeout(10000).times(1)).save(Mockito.any());
        Thread.sleep(5000);

        // Verify, that submission updated
        SubmissionEntity submissionEntity = submissionService.getSubmissionEntity(submissionId);
        Assertions.assertEquals(1, submissionEntity.getScore());
        Assertions.assertEquals(SubmissionStatus.WRONG_ANSWER.getValue(), submissionEntity.getStatus());

        // Verify, that source code was uploaded to s3
        String actualSourceCode = sourceCodeService.downloadSourceCode(submissionId);
        Assertions.assertEquals(sourceCode, actualSourceCode);
    }

    // Submit message failed: not valid message
    @Test
    void givenInvalidSubmissionMessage_whenSubmitMessage_thenSendToDLQ() throws Exception {
        // Assign
        long userId = -1;
        long contestId = 2;
        long problemId = 3;
        SubmissionId submissionId = new SubmissionId(userId, contestId, problemId);
        String sourceCode = "print('Hello world')";
        String language = "Python (3.8.1)";
        OffsetDateTime submittedAt = OffsetDateTime.now();
        SubmissionMessage submissionMessage = new SubmissionMessage(userId, contestId, problemId, sourceCode, language, submittedAt);

        List<Language> expectedLanguages = List.of(
                new Language(1, "Java"),
                new Language(2, "C++"),
                new Language(3, "Python (3.8.1)")
        );
        String languagesRequestBody = objectMapper.writeValueAsString(expectedLanguages);
        WireMock.stubFor(
                WireMock.get("/languages")
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(200)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(languagesRequestBody)
                        )
        );

        // Act
        rabbitTemplate.convertAndSend(submissionQueueName, submissionMessage);

        // Assert
        Mockito.verify(dlqListener, Mockito.timeout(10000).times(1)).processInvalidMessage(Mockito.any());
    }

    public static class DLQListener {
        @RabbitListener(queues = "${app.rabbit-mq.submission-dead-letter-queue-name}")
        public void processInvalidMessage(SubmissionMessage submissionMessage) {
        }
    }

    @TestConfiguration
    public static class RabbitConfiguration {
        @Bean
        public RabbitTemplate rabbitTemplate(
                ConnectionFactory connectionFactory,
                Jackson2JsonMessageConverter rabbitProducerMessageConverter
        ) {
            RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
            rabbitTemplate.setMessageConverter(rabbitProducerMessageConverter);
            return rabbitTemplate;
        }

        @Bean
        public DLQListener dlqListener() {
            return new DLQListener();
        }
    }

}
