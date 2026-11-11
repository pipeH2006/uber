package com.uber.notificaciones.application.port.in;

import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.TipoNotificacion;

import java.util.List;

/**
 * Caso de uso que envía una notificación a múltiples destinatarios.
 * El fallo de un destinatario no aborta el envío del resto.
 */
public interface EnviarNotificacionMasivaUseCase {

    List<Notificacion> enviarMasivo(List<String> destinatarios, String mensaje, TipoNotificacion tipo);
}
