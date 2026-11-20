package com.uber.notificaciones.domain.usecase;

import com.uber.notificaciones.domain.exception.EnvioNotificacionException;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.gateway.NotificacionGateway;
import com.uber.notificaciones.domain.model.gateway.TelegramGateway;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class NotificacionSender {
    private final NotificacionGateway notificacionGateway;
    private final TelegramGateway telegramGateway;

    public Notificacion enviar(Notificacion notificacion) {
        notificacion.setIntentos(notificacion.getIntentos() + 1);
        try {
            telegramGateway.enviar(notificacion.getDestinatario(), notificacion.getMensaje());
            notificacion.setEstado(EstadoNotificacion.ENVIADA);
            notificacion.setFechaEnvio(LocalDateTime.now());
        } catch (EnvioNotificacionException e) {
            notificacion.setEstado(EstadoNotificacion.FALLIDA);
            notificacionGateway.guardar(notificacion);
            throw e;
        }
        return notificacionGateway.guardar(notificacion);
    }
}
