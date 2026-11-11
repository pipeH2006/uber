package com.uber.notificaciones.application.port.in;

import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.TipoNotificacion;

/**
 * Caso de uso que crea y envía una notificación individual.
 */
public interface EnviarNotificacionUseCase {

    Notificacion enviar(String destinatario, String mensaje, TipoNotificacion tipo);
}
