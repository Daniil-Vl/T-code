package ru.tbank.submissionservice.exception.test_case;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.tbank.submissionservice.exception.CustomException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTestCasesException extends CustomException {
    public InvalidTestCasesException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
