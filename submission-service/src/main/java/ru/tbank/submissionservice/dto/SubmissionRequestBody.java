package ru.tbank.submissionservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.tbank.submissionservice.validation.annotations.LanguageConstraint;

public record SubmissionRequestBody(
        @NotBlank
        @JsonProperty(value = "source_code")
        String sourceCode,

        @NotBlank
        @LanguageConstraint
        @JsonProperty(value = "language")
        String language,

        @NotNull
        @JsonProperty(value = "stdin")
        String stdin
) {
}
