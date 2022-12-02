package com.leolebleis.card.helpers.errors.token;

import io.micronaut.http.HttpStatus;

/**
 * Custom exception thrown with errors happening around Token creation/access.
 * If a HttpStatus is not specified,  status is set to 400 BAD_REQUEST.
 */
public class TokenException extends RuntimeException {

    private final HttpStatus status;

    public TokenException() {
        super();
        this.status = HttpStatus.BAD_REQUEST;
    }

    public TokenException(final String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public TokenException(final String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public TokenException(final HttpStatus status) {
        super();
        this.status = status;
    }

    public TokenException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }

    public TokenException(final String message, Throwable cause, final HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

}
