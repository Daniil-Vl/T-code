package ru.tbank.submissionservice.enums;

import lombok.Getter;

@Getter
public enum SubmissionStatus {
    IN_QUEUE("In Queue"),
    PROCESSING("Processing"),
    ACCEPTED("Accepted"),
    WRONG_ANSWER("Wrong Answer"),
    TIME_LIMIT_EXCEEDED("Time Limit Exceeded"),
    COMPILATION_ERROR("Compilation Error"),
    RUNTIME_ERROR("Runtime Error"),
    INTERNAL_ERROR("Internal Error"),
    EXEC_FORMAT_ERROR("Exec Format Error");

    private final String value;

    SubmissionStatus(String value) {
        this.value = value;
    }
}
