package ru.tbank.submissionservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubmissionResult(
        String stdout,

        @JsonProperty(value = "time")
        Double runningTimeSeconds,

        @JsonProperty(value = "memory")
        Double runningMemoryKB,

        String stderr,
        String compileOutput,
        String message,
        Status status
) {
    public record Status(long id, String description) {
    }
}
