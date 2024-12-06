package ru.tbank.contestservice.dto.submission;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record SubmissionMessage(
        @JsonProperty("user_id") long userId,
        @JsonProperty("contest_id") long contestId,
        @JsonProperty("problem_id") long problemId,

        @JsonProperty("source_code") String sourceCode,
        @JsonProperty("language") String language,

        @JsonProperty("submitted_at")
        OffsetDateTime submittedAt
) {
}
