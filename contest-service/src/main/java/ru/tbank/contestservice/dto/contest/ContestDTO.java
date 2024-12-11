package ru.tbank.contestservice.dto.contest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.tbank.contestservice.model.entities.ContestEntity;
import ru.tbank.contestservice.utils.serialization.OffsetDateTimeSerializer;

import java.time.OffsetDateTime;

public record ContestDTO(
        @JsonProperty("contest_id")
        long contestId,

        @JsonProperty("title")
        String title,

        @JsonSerialize(using = OffsetDateTimeSerializer.class)
        @JsonProperty("start_time")
        OffsetDateTime startTime,

        @JsonSerialize(using = OffsetDateTimeSerializer.class)
        @JsonProperty("end_time")
        OffsetDateTime endTime
) {
    public static ContestDTO fromEntity(ContestEntity contest) {
        return new ContestDTO(
                contest.getId(),
                contest.getTitle(),
                contest.getStartDateTime(),
                contest.getEndDateTime()
        );
    }
}
