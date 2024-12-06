package ru.tbank.submissionservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubmissionResult(
        String stdout,

        @JsonProperty(value = "time")
        Double runningTimeSeconds,

        @JsonProperty(value = "memory")
        Double runningMemoryKB,

        String stderr,
        String token,
        String compileOutput,
        String message,
        Status status
) {
    private static final String IN_QUEUE_STATUS_MESSAGE = "In Queue";
    private static final String PROCESSING_STATUS_MESSAGE = "Processing";
    public static final String ACCEPTED_STATUS_MESSAGE = "Accepted";

    public boolean isCompleted() {
        String description = status().description();
        return !description.equals(IN_QUEUE_STATUS_MESSAGE)
                && !description.equals(PROCESSING_STATUS_MESSAGE);
    }

    public boolean isAccepted() {
        return status.description().equals(ACCEPTED_STATUS_MESSAGE);
    }

    public record Status(long id, String description) {
    }
}
