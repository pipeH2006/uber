package com.uber.pagos.domain.exception;

public class ErrorExternoException extends RuntimeException {

    public ErrorExternoException(String message) {
        super(message);
    }

    public ErrorExternoException(String message, Throwable cause) {
        super(message, cause);
    }
}
