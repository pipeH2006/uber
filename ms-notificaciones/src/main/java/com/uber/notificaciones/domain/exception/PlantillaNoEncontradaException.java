package com.uber.notificaciones.domain.exception;

public class PlantillaNoEncontradaException extends RuntimeException {

    public PlantillaNoEncontradaException(String codigo) {
        super("Plantilla no encontrada con código: " + codigo);
    }
}
