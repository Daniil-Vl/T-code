package ru.tbank.contestservice.exception.problem;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.tbank.contestservice.exception.ResourceNotFoundException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProblemNotFoundException extends ResourceNotFoundException {
    public ProblemNotFoundException(String message) {
        super(message);
    }
}
