package ru.tbank.submissionservice.exception.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.tbank.submissionservice.dto.error.ApiErrorResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class S3ExceptionHandler {

    @ExceptionHandler(NoSuchKeyException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNoSuchKeyException(NoSuchKeyException e) {
        return new ApiErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.toString(),
                "Object with given id do not found"
        );
    }

}
