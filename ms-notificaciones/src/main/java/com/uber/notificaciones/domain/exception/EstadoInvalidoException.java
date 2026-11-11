package com.uber.notificaciones.domain.exception;

/**
 * Se lanza cuando se intenta ejecutar una transición de estado no permitida
 * sobre una notificación (por ejemplo, reintentar una notificación que no
 * está en estado FALLIDA).
 */
public class EstadoInvalidoException extends RuntimeException {

    public EstadoInvalidoException(String message) {
        super(message);
    }
}
