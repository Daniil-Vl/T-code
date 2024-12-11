package ru.tbank.submissionservice.dto.submission;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubmissionRequestDTO(
        @JsonProperty("source_code")
        String sourceCode,

        @JsonProperty("language_id")
        int languageId,

        @JsonProperty("stdin")
        String stdin,

        @JsonProperty("expected_output")
        String expectedOutput
) {
}
