package ru.tbank.submissionservice.dto;

public record ApiErrorResponse(
        int status,
        String message
) {
}
