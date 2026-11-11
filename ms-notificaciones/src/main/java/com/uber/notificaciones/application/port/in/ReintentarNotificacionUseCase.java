package com.uber.notificaciones.application.port.in;

import com.uber.notificaciones.domain.model.Notificacion;

/**
 * Caso de uso para reintentar el envío de una notificación fallida.
 */
public interface ReintentarNotificacionUseCase {

    Notificacion reintentar(Long id);
}
