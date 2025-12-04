package dev.revature.fantasy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a league id is invalid on a request to backend
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class InvalidLeagueIdException extends Exception {
    public InvalidLeagueIdException(String message) {
        super(message);
    }
    
}
