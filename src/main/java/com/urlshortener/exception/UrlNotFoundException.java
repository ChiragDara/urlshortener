package com.urlshortener.exception;

/**
 * Custom exception thrown when a shortened URL is not found in the system.
 */
public class UrlNotFoundException extends RuntimeException {

    public UrlNotFoundException(String message) {
        super(message);
    }

    public UrlNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
