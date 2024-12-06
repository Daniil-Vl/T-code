package ru.tbank.contestservice.exception.contest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.tbank.contestservice.exception.CustomException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ContestTimeException extends CustomException {
    public ContestTimeException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
