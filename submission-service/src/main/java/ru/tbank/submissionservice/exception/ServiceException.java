package ru.tbank.submissionservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ServiceException extends CustomException {

    private final HttpStatus httpStatus;

    public ServiceException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    @Override
    public HttpStatus getStatusCode() {
        return httpStatus;
    }

}
