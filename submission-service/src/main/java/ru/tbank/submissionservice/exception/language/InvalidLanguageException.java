package ru.tbank.submissionservice.exception.language;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.tbank.submissionservice.exception.CustomException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidLanguageException extends CustomException {
    public InvalidLanguageException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
