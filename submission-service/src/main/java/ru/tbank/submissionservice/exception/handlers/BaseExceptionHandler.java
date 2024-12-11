package ru.tbank.submissionservice.exception.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import ru.tbank.submissionservice.dto.error.ApiErrorResponse;

import java.time.OffsetDateTime;

@RestControllerAdvice
@Slf4j
public class BaseExceptionHandler {

    @ExceptionHandler({
            HandlerMethodValidationException.class,
            MethodArgumentNotValidException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidationException(
            ErrorResponse errorResponse
    ) {
        StringBuilder message = new StringBuilder();

        for (var messageArgument : errorResponse.getDetailMessageArguments()) {
            String fieldError = messageArgument.toString();
            if (!fieldError.isBlank()) {
                message.append(fieldError).append(", ");
            }
        }

        return new ApiErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.toString(),
                message.substring(0, message.length() - 2)
        );
    }

}
