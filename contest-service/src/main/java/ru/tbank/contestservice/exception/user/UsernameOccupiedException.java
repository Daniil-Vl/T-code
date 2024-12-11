package ru.tbank.contestservice.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.tbank.contestservice.exception.CustomException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UsernameOccupiedException extends CustomException {
    public UsernameOccupiedException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
