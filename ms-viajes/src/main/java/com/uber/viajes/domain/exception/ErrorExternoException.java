package com.uber.viajes.domain.exception;

public class ErrorExternoException extends RuntimeException {

    public ErrorExternoException(String message) {
        super(message);
    }
}
