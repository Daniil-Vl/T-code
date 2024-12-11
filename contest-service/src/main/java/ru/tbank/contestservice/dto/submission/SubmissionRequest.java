package ru.tbank.contestservice.dto.submission;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import ru.tbank.contestservice.utils.validation.annotations.LanguageConstraint;

public record SubmissionRequest(
        @NotBlank(message = "source code cannot be empty")
        @JsonProperty("source_code")
        String sourceCode,

        @NotBlank(message = "language cannot be empty")
        @LanguageConstraint
        @JsonProperty("language")
        String language
) {
}
