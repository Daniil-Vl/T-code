package ru.tbank.submissionservice.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import ru.tbank.submissionservice.client.Judge0Client;
import ru.tbank.submissionservice.dto.SubmissionRequestDTO;
import ru.tbank.submissionservice.dto.SubmissionResult;
import ru.tbank.submissionservice.dto.SubmissionToken;
import ru.tbank.submissionservice.enums.Language;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class Judge0ClientImpl implements Judge0Client {

    private static final String LANGUAGES_PATH = "/languages";
    private static final String SUBMISSION_PATH = "/submissions";
    private static final String SUBMISSION_BATCH_PATH = "/submissions/batch";
    private static final String SUBMISSION_TOKEN_PATH = "/submissions/{submission_token}";
    private static final String BASE64_ENCODED_PARAM_NAME = "base64_encoded";
    private static final String WAIT_PARAM_NAME = "wait";
    private static final String TOKENS_PARAM_NAME = "tokens";

    private final WebClient webClient;
    private final Retry retry;

    @Override
    public SubmissionToken submit(String sourceCode, int languageId, String stdin) {
        log.info("Submitting to judge0");

        SubmissionRequestDTO requestBody = new SubmissionRequestDTO(sourceCode, languageId, stdin);

        return webClient
                .post()
                .uri(
                        uriBuilder -> uriBuilder
                                .path(SUBMISSION_PATH)
                                .queryParam(BASE64_ENCODED_PARAM_NAME, false)
                                .queryParam(WAIT_PARAM_NAME, false)
                                .build()
                )
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(SubmissionToken.class)
                .retryWhen(retry)
                .block();
    }

    @Override
    public Mono<SubmissionResult> submitWaiting(String sourceCode, int languageId, String stdin) {
        log.info("Submitting to judge0 with waiting");

        SubmissionRequestDTO requestBody = new SubmissionRequestDTO(sourceCode, languageId, stdin);

        return webClient
                .post()
                .uri(
                        uriBuilder -> uriBuilder
                                .path(SUBMISSION_PATH)
                                .queryParam(BASE64_ENCODED_PARAM_NAME, false)
                                .queryParam(WAIT_PARAM_NAME, true)
                                .build()
                )
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(SubmissionResult.class)
                .retryWhen(retry);
    }

    @Override
    public List<SubmissionToken> submitBatch(List<SubmissionRequestDTO> submissionRequests) {
        log.info("Submitting batch to judge0");

        SubmissionBatchDTO requestBody = new SubmissionBatchDTO(submissionRequests);

        return webClient
                .post()
                .uri(
                        uriBuilder -> uriBuilder
                                .path(SUBMISSION_BATCH_PATH)
                                .queryParam(BASE64_ENCODED_PARAM_NAME, false)
                                .build()
                )
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(
                        new ParameterizedTypeReference<List<SubmissionToken>>() {
                        }
                )
                .retryWhen(retry)
                .block();
    }

    @Override
    public SubmissionResult getSubmissionResult(String submissionToken) {
        log.info("Getting submission result from judge0");
        return webClient
                .get()
                .uri(
                        uriBuilder -> uriBuilder
                                .path(SUBMISSION_TOKEN_PATH)
                                .build(submissionToken)
                )
                .retrieve()
                .bodyToMono(SubmissionResult.class)
                .retryWhen(retry)
                .block();
    }

    @Override
    public List<SubmissionResult> getBatchSubmissionResults(List<SubmissionToken> tokens) {
        log.info("Getting submission results from judge0 for multiple tokens");
        return webClient
                .get()
                .uri(
                        uriBuilder -> uriBuilder
                                .path(SUBMISSION_BATCH_PATH)
                                .queryParam(BASE64_ENCODED_PARAM_NAME, false)
                                .queryParam(TOKENS_PARAM_NAME, String.join(",", tokens.stream().map(SubmissionToken::token).toList()))
                                .build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<SubmissionResult>>() {
                })
                .retryWhen(retry)
                .block();
    }

    @Override
    @Cacheable(cacheNames = "languages-cache")
    public List<Language> getLanguages() {
        log.info("Request available languages from judge0...");

        List<Language> languages = webClient
                .get()
                .uri(LANGUAGES_PATH)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Language>>() {
                })
                .retryWhen(retry)
                .block();

        return languages
                .stream()
                .map(
                        language -> new Language(
                                language.id(),
                                Language.getNameWithoutCompiler(language.name()).toLowerCase()
                        )
                )
                .toList();
    }

    private record SubmissionBatchDTO(List<SubmissionRequestDTO> submissions) {
    }
}
