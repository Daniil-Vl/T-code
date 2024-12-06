package ru.tbank.contestservice.exception.contest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.tbank.contestservice.exception.ResourceNotFoundException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ContestNotFoundException extends ResourceNotFoundException {
    public ContestNotFoundException(String message) {
        super(message);
    }
}
