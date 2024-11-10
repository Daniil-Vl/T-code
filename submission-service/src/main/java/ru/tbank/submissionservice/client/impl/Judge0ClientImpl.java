package ru.tbank.submissionservice.client.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import ru.tbank.submissionservice.client.Judge0Client;
import ru.tbank.submissionservice.dto.SubmissionRequestBody;
import ru.tbank.submissionservice.dto.SubmissionResult;
import ru.tbank.submissionservice.dto.SubmissionToken;
import ru.tbank.submissionservice.enums.Language;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class Judge0ClientImpl implements Judge0Client {

    private final WebClient webClient;
    private final Retry retry;

    @Override
    public SubmissionToken submit(String sourceCode, Language language, String stdin) {
        log.info("Submitting to judge0");

        SubmissionRequestDTO requestBody = new SubmissionRequestDTO(sourceCode, language.getLanguageId(), stdin);

        return webClient
                .post()
                .uri(
                        uriBuilder -> uriBuilder
                                .path("/submissions")
                                .queryParam("base64_encoded", false)
                                .queryParam("wait", false)
                                .build()
                )
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(SubmissionToken.class)
                .retryWhen(retry)
                .block();
    }

    @Override
    public Mono<SubmissionResult> submitWaiting(String sourceCode, Language language, String stdin) {
        log.info("Submitting to judge0 with waiting");

        SubmissionRequestDTO requestBody = new SubmissionRequestDTO(sourceCode, language.getLanguageId(), stdin);

        return webClient
                .post()
                .uri(
                        uriBuilder -> uriBuilder
                                .path("/submissions")
                                .queryParam("base64_encoded", false)
                                .queryParam("wait", true)
                                .build()
                )
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(SubmissionResult.class)
                .retryWhen(retry);
    }

    @Override
    public List<SubmissionToken> submitBatch(List<SubmissionRequestBody> submissionRequests) {
        log.info("Submitting batch to judge0");

        SubmissionBatchDTO requestBody = new SubmissionBatchDTO(
                submissionRequests.stream().map(SubmissionRequestDTO::from).toList()
        );

        return webClient
                .post()
                .uri(
                        uriBuilder -> uriBuilder
                                .path("/submissions/batch")
                                .queryParam("base64_encoded", false)
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
                                .path("/submissions/{submission_token}")
                                .build(submissionToken)
                )
                .retrieve()
                .bodyToMono(SubmissionResult.class)
                .retryWhen(retry)
                .block();
    }

    private record SubmissionRequestDTO(
            @JsonProperty("source_code")
            String sourceCode,
            @JsonProperty("language_id")
            int languageId,
            @JsonProperty("stdin")
            String stdin
    ) {
        public static SubmissionRequestDTO from(SubmissionRequestBody submissionRequestBody) {
            return new SubmissionRequestDTO(
                    submissionRequestBody.sourceCode(),
                    Language.valueOf(submissionRequestBody.language().toUpperCase()).getLanguageId(),
                    submissionRequestBody.stdin()
            );
        }
    }

    private record SubmissionBatchDTO(List<SubmissionRequestDTO> submissions) {
    }
}
