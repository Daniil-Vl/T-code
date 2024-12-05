package ru.tbank.submissionservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubmissionRequestDTO(
        @JsonProperty("source_code")
        String sourceCode,

        @JsonProperty("language_id")
        int languageId,

        @JsonProperty("stdin")
        String stdin
) {
}
