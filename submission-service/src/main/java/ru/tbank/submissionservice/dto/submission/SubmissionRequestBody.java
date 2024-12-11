package ru.tbank.submissionservice.dto.submission;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.tbank.submissionservice.utils.validation.annotations.LanguageConstraint;

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
        String stdin,

        @NotNull
        @JsonProperty(value = "expected_output")
        String expectedOutput
) {
}
