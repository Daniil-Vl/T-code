package ru.tbank.submissionservice.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import ru.tbank.submissionservice.client.Judge0Client;
import ru.tbank.submissionservice.dto.submission.SubmissionRequestDTO;
import ru.tbank.submissionservice.dto.submission.SubmissionResults;
import ru.tbank.submissionservice.dto.submission.SubmissionToken;
import ru.tbank.submissionservice.dto.language.Language;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class Judge0ClientImpl implements Judge0Client {

    private static final String LANGUAGES_PATH = "/languages";
    private static final String SUBMISSION_BATCH_PATH = "/submissions/batch";
    private static final String BASE64_ENCODED_PARAM_NAME = "base64_encoded";
    private static final String TOKENS_PARAM_NAME = "tokens";

    private final WebClient webClient;
    private final Retry retry;

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
    public SubmissionResults getBatchSubmissionResults(List<SubmissionToken> tokens) {
        log.info("Getting submission results from judge0 for multiple tokens = {}", tokens);
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
                .bodyToMono(SubmissionResults.class)
                .retryWhen(retry)
                .block();
    }

    @Override
    @Cacheable(cacheNames = "languages-cache")
    public List<Language> getLanguages() {
        log.info("Request available languages from judge0...");

        return webClient
                .get()
                .uri(LANGUAGES_PATH)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Language>>() {
                })
                .retryWhen(retry)
                .block();
    }

    private record SubmissionBatchDTO(List<SubmissionRequestDTO> submissions) {
    }
}
