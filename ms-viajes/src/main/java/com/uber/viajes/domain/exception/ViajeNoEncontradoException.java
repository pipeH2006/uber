package com.uber.viajes.domain.exception;

public class ViajeNoEncontradoException extends RuntimeException {

    public ViajeNoEncontradoException(Long id) {
        super("Viaje no encontrado con id: " + id);
    }
}
