package com.uber.usuarios.domain.exception;

public class EstadoInvalidoException extends RuntimeException {

    public EstadoInvalidoException(String message) {
        super(message);
    }
}
