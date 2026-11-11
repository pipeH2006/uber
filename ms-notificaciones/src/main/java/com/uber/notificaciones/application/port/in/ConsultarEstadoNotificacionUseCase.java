package com.uber.notificaciones.application.port.in;

import com.uber.notificaciones.domain.model.Notificacion;

/**
 * Caso de uso para consultar el estado de una notificación.
 */
public interface ConsultarEstadoNotificacionUseCase {

    Notificacion estado(Long id);
}
