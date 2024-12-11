package ru.tbank.submissionservice.dto.submission;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SubmissionResults(
        @JsonProperty("submissions")
        List<SubmissionResult> submissions
) {
}
