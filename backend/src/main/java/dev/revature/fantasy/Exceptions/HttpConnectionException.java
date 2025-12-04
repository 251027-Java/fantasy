package dev.revature.fantasy.exceptions;

import java.net.ConnectException;

/**
 * Exception thrown when a connection to sleeper api fails
 */
public class HttpConnectionException extends ConnectException {
    public HttpConnectionException(String message) {
        super(message);
    }
}
