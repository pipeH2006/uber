package com.uber.notificaciones.domain.usecase;

import com.uber.notificaciones.domain.exception.NotificacionNoEncontradaException;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.gateway.NotificacionGateway;
import lombok.RequiredArgsConstructor;

/**
 * Caso de uso para consultar el estado de una notificación.
 */
@RequiredArgsConstructor
public class ConsultarEstadoNotificacionUseCase {

    private final NotificacionGateway notificacionGateway;

    public Notificacion estado(Long id) {
        return notificacionGateway.buscarPorId(id)
                .orElseThrow(() -> new NotificacionNoEncontradaException(id));
    }
}
