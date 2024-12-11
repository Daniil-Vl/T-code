package ru.tbank.contestservice.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import ru.tbank.contestservice.dto.error.ApiErrorResponse;
import ru.tbank.contestservice.exception.CustomException;
import ru.tbank.contestservice.exception.ForbiddenException;
import ru.tbank.contestservice.exception.ResourceNotFoundException;
import ru.tbank.contestservice.exception.contest.ContestNotFoundException;
import ru.tbank.contestservice.exception.contest.ContestTimeException;
import ru.tbank.contestservice.exception.problem.ProblemNotFoundException;
import ru.tbank.contestservice.exception.user.UserNotFoundException;
import ru.tbank.contestservice.exception.user.UsernameOccupiedException;

import java.time.OffsetDateTime;

@RestControllerAdvice
@Slf4j
public class BaseExceptionHandler {

    @ExceptionHandler({
            UserNotFoundException.class,
            ContestNotFoundException.class,
            ProblemNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNotFoundException(ResourceNotFoundException e) {
        return handleException(e);
    }

    @ExceptionHandler({
            UsernameOccupiedException.class,
            ContestTimeException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleBadRequestException(CustomException e) {
        return handleException(e);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiErrorResponse handleForbiddenException(ForbiddenException e) {
        return handleException(e);
    }

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

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleException(Exception e) {
        log.error("Handle unexpected exception: ", e);
        return new ApiErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                e.getMessage()
        );
    }

    private ApiErrorResponse handleException(CustomException e) {
        return new ApiErrorResponse(
                OffsetDateTime.now(),
                e.getStatusCode().value(),
                e.getStatusCode().toString(),
                e.getMessage()
        );
    }

}
