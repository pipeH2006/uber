package com.uber.viajes.domain.exception;

public class UsuarioNoHabilitadoException extends RuntimeException {

    public UsuarioNoHabilitadoException(String message) {
        super(message);
    }
}
