package com.uber.notificaciones.domain.usecase;

import com.uber.notificaciones.domain.exception.EstadoInvalidoException;
import com.uber.notificaciones.domain.exception.NotificacionNoEncontradaException;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.gateway.NotificacionGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReintentarNotificacionUseCase {
    private final NotificacionGateway notificacionGateway;
    private final NotificacionSender sender;

    public Notificacion reintentar(Long id) {
        Notificacion notificacion = notificacionGateway.buscarPorId(id)
                .orElseThrow(() -> new NotificacionNoEncontradaException(id));
        if (notificacion.getEstado() != EstadoNotificacion.FALLIDA) {
            throw new EstadoInvalidoException("Solo se puede reintentar una notificación en estado FALLIDA");
        }
        return sender.enviar(notificacion);
    }
}
