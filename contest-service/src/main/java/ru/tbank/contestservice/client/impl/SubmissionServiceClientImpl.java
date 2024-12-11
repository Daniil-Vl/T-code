package ru.tbank.contestservice.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.tbank.contestservice.client.SubmissionServiceClient;
import ru.tbank.contestservice.dto.TestCase;
import ru.tbank.contestservice.dto.contest.ContestResult;
import ru.tbank.contestservice.dto.contest.UserRating;
import ru.tbank.contestservice.dto.submission.SubmissionMessage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionServiceClientImpl implements SubmissionServiceClient {

    private static final String LANGUAGES_PATH = "/api/v1/languages";
    private static final String TEST_CASE_PATH = "/api/v1/test-case/problem/{problem_id}";
    private static final String CONTEST_RESULT_PATH = "/api/v1/contest/{contest_id}/result";
    private static final String CONTEST_RATING_PATH = "/api/v1/contest/{contest_id}/rating";

    private final WebClient webClient;
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbit-mq.submission-queue-name}")
    private String submissionQueueName;

    @Override
    @Cacheable(cacheResolver = "languageCacheResolver")
    public Set<String> getAvailableLanguages() {
        log.info("Retrieve available languages from submission service");

        List<String> languages = webClient
                .get()
                .uri(LANGUAGES_PATH)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .block();

        return new HashSet<>(languages);
    }

    @Override
    public void submit(SubmissionMessage submissionMessage) {
        log.info("Send submission message {} to queue {}", submissionMessage, submissionQueueName);
        rabbitTemplate.convertAndSend(submissionQueueName, submissionMessage);
    }

    @Async
    @Override
    public CompletableFuture<Void> saveTestCases(long problemId, List<TestCase> testCases) {
        log.info("Save test cases: {}", testCases);

        return webClient
                .post()
                .uri(TEST_CASE_PATH, problemId)
                .bodyValue(testCases)
                .retrieve()
                .bodyToMono(Void.class)
                .toFuture();
    }

    @Async
    @Override
    public CompletableFuture<Void> updateTestCases(long problemId, List<TestCase> testCases) {
        log.info("Update test cases: {}", testCases);
        return saveTestCases(problemId, testCases);
    }

    @Override
    public List<TestCase> getTestCases(long problemId) {
        log.info("Request test cases for problem with id {} from submission service", problemId);

        return webClient
                .get()
                .uri(TEST_CASE_PATH, problemId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<TestCase>>() {
                })
                .block();
    }

    @Override
    public ContestResult getContestResult(long contestId) {
        log.info("Requesting results of contest {} from submission service", contestId);

        return webClient
                .get()
                .uri(CONTEST_RESULT_PATH, contestId)
                .retrieve()
                .bodyToMono(ContestResult.class)
                .block();
    }

    @Override
    public UserRating getUserRating(long contestId) {
        log.info("Requesting user rating of contest {} from submission service", contestId);

        return webClient
                .get()
                .uri(CONTEST_RATING_PATH, contestId)
                .retrieve()
                .bodyToMono(UserRating.class)
                .block();
    }

}
