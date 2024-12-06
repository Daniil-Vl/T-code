package ru.tbank.contestservice.dto.contest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;
import java.util.List;

public record CreateContestRequest(
        @NotBlank(message = "title cannot be empty")
        @JsonProperty("title")
        String title,

        @JsonProperty("start_time")
        OffsetDateTime startTime,

        @JsonProperty("end_time")
        OffsetDateTime endTime,

        @JsonProperty("problem_ids")
        List<Long> problemIds
) {
}
