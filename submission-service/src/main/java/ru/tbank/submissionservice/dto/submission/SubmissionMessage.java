package ru.tbank.submissionservice.dto.submission;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import ru.tbank.submissionservice.utils.validation.annotations.LanguageConstraint;

import java.time.OffsetDateTime;

public record SubmissionMessage(
        @JsonProperty("user_id")
        @Positive(message = "user id must be positive")
        long userId,

        @JsonProperty("contest_id")
        @Positive(message = "contest id must be positive")
        long contestId,

        @JsonProperty("problem_id")
        @Positive(message = "problem id must be positive")
        long problemId,

        @JsonProperty("source_code")
        @NotBlank(message = "source code cannot be blank")
        String sourceCode,

        @JsonProperty("language")
        @NotBlank(message = "language cannot be blank")
        @LanguageConstraint
        String language,

        @JsonProperty("submitted_at")
        OffsetDateTime submittedAt
) {
}
