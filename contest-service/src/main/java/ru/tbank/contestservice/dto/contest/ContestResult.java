package ru.tbank.contestservice.dto.contest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.tbank.contestservice.utils.serialization.OffsetDateTimeSerializer;

import java.time.OffsetDateTime;
import java.util.List;

public record ContestResult(
        List<ProblemResult> problemResults
) {
    public record ProblemResult(
            @JsonProperty("username") String nickname,
            @JsonProperty("status") String status,

            @JsonProperty("language") String language,

            @JsonSerialize(using = OffsetDateTimeSerializer.class)
            @JsonProperty("submitted_at")
            OffsetDateTime submittedAt,

            @JsonProperty("submission_id") long submissionId,

            @JsonProperty("execution_time_ms") long executionTimeMs,
            @JsonProperty("memory_used_kb") long memoryUsedKb
    ) {
    }
}
