package ru.tbank.submissionservice.exception.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.tbank.submissionservice.dto.ApiErrorResponse;
import ru.tbank.submissionservice.exception.ServiceException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class SubmissionExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getFieldErrors()
                        .stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .collect(Collectors.joining("; "))
        );
    }

    @ExceptionHandler(value = ServiceException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiErrorResponse handleServiceException(ServiceException exception) {
        return new ApiErrorResponse(
                exception.getStatusCode(),
                exception.getMessage()
        );
    }

}
