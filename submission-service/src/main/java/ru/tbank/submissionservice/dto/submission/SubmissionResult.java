package ru.tbank.submissionservice.dto.submission;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tbank.submissionservice.enums.SubmissionStatus;

public record SubmissionResult(
        String stdout,

        @JsonProperty(value = "time")
        double runningTimeSeconds,

        @JsonProperty(value = "memory")
        double runningMemoryKB,

        String stderr,
        String token,
        String compileOutput,
        String message,
        Status status
) {
    public boolean isInProcess() {
        String description = status().description();
        return description.equals(SubmissionStatus.IN_QUEUE.getValue())
                || description.equals(SubmissionStatus.PROCESSING.getValue());
    }

    public boolean isAccepted() {
        return status.description().equals(SubmissionStatus.ACCEPTED.getValue());
    }

    public record Status(long id, String description) {
    }
}
