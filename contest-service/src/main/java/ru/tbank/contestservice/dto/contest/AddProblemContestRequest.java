package ru.tbank.contestservice.dto.contest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;

public record AddProblemContestRequest(
        @Positive(message = "contest id must be positive")
        @JsonProperty("contest_id")
        long contestId,

        @Positive(message = "problem id must be positive")
        @JsonProperty("problem_id")
        long problemId
) {
}
