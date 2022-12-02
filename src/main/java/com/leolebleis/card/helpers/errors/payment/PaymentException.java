package com.leolebleis.card.helpers.errors.payment;

import io.micronaut.http.HttpStatus;

/**
 * Custom exception thrown with errors happening around Payment Authorization.
 * If a HttpStatus is not specified,  status is set to 400 BAD_REQUEST.
 */
public class PaymentException extends RuntimeException {

    private final HttpStatus status;

    public PaymentException() {
        super();
        this.status = HttpStatus.BAD_REQUEST;
    }

    public PaymentException(final String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public PaymentException(final String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public PaymentException(final HttpStatus status) {
        super();
        this.status = status;
    }

    public PaymentException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }

    public PaymentException(final String message, Throwable cause, final HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

}
