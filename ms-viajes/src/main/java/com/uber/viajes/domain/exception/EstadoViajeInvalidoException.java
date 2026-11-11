package com.uber.viajes.domain.exception;

public class EstadoViajeInvalidoException extends RuntimeException {

    public EstadoViajeInvalidoException(String message) {
        super(message);
    }
}
