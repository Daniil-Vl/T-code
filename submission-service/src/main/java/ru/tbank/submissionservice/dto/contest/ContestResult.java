package ru.tbank.submissionservice.dto.contest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.tbank.submissionservice.model.entities.SubmissionEntity;
import ru.tbank.submissionservice.utils.serialization.OffsetDateTimeDeserializer;
import ru.tbank.submissionservice.utils.serialization.OffsetDateTimeSerializer;

import java.time.OffsetDateTime;
import java.util.List;

public record ContestResult(
        @JsonProperty("contest_id")
        long contestId,

        @JsonProperty("results")
        List<ProblemResult> problemResults
) {
    public record ProblemResult(
            @JsonProperty("problem_id") long problemId,
            @JsonProperty("user_id") long userId,
            @JsonProperty("status") String status,

            @JsonProperty("language") String language,

            @JsonSerialize(using = OffsetDateTimeSerializer.class)
            @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
            @JsonProperty("submitted_at")
            OffsetDateTime submittedAt,

            @JsonProperty("score") long score,
            @JsonProperty("execution_time_ms") long executionTimeMs,
            @JsonProperty("memory_used_kb") long memoryUsedKb
    ) {
        public static ProblemResult fromEntity(SubmissionEntity submission) {
            return new ProblemResult(
                    submission.getProblemId(),
                    submission.getUserId(),
                    submission.getStatus(),
                    submission.getLanguage(),
                    submission.getSubmittedAt(),
                    submission.getScore(),
                    submission.getExecutionTimeMs(),
                    submission.getMemoryUsedKb()
            );
        }
    }
}
