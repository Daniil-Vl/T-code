package ru.tbank.submissionservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidLanguageException extends RuntimeException {
    public InvalidLanguageException(String message) {
        super(message);
    }
}
