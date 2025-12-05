package dev.revature.fantasy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a username is invalid on a request to backend
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvalidUsernameException extends Exception {
    public InvalidUsernameException(String message) {
        super(message);
    }
}
