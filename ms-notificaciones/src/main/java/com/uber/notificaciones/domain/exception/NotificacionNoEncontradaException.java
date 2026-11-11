package com.uber.notificaciones.domain.exception;

public class NotificacionNoEncontradaException extends RuntimeException {

    public NotificacionNoEncontradaException(Long id) {
        super("Notificación no encontrada con id: " + id);
    }
}
