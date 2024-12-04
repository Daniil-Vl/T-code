package ru.tbank.submissionservice.exception.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.tbank.submissionservice.dto.ApiErrorResponse;
import ru.tbank.submissionservice.exception.InvalidTestCasesException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@RestControllerAdvice
public class S3ExceptionHandler {

    @ExceptionHandler(NoSuchKeyException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNoSuchKeyException(NoSuchKeyException e) {
        return new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Object with given id do not found"
        );
    }

    @ExceptionHandler(InvalidTestCasesException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleInvalidTestCasesException(InvalidTestCasesException e) {
        return new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
        );
    }

}
