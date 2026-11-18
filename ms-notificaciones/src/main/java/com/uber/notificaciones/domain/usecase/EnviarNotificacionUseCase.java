package com.uber.notificaciones.domain.usecase;

import com.uber.notificaciones.domain.model.Canal;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.TipoNotificacion;
import com.uber.notificaciones.domain.model.gateway.NotificacionGateway;
import lombok.RequiredArgsConstructor;

/**
 * Caso de uso que crea y envía una notificación individual.
 */
@RequiredArgsConstructor
public class EnviarNotificacionUseCase {

    private final NotificacionGateway notificacionGateway;
    private final NotificacionSender sender;

    public Notificacion enviar(String destinatario, String mensaje, TipoNotificacion tipo) {
        Notificacion notificacion = Notificacion.builder()
                .destinatario(destinatario)
                .canal(Canal.TELEGRAM)
                .tipo(tipo == null ? TipoNotificacion.GENERICO : tipo)
                .mensaje(mensaje)
                .estado(EstadoNotificacion.PENDIENTE)
                .intentos(0)
                .build();
        Notificacion guardada = notificacionGateway.guardar(notificacion);
        return sender.enviar(guardada);
    }
}
