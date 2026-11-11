package com.uber.notificaciones.application.service;

import com.uber.notificaciones.application.port.out.NotificacionRepositoryPort;
import com.uber.notificaciones.application.port.out.TelegramPort;
import com.uber.notificaciones.domain.exception.EnvioNotificacionException;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.Notificacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Componente de aplicación que ejecuta el intento real de envío de una
 * notificación y actualiza su estado en consecuencia.
 */
@Component
@RequiredArgsConstructor
public class NotificacionSender {

    private final NotificacionRepositoryPort repository;
    private final TelegramPort telegramPort;

    /**
     * Intenta enviar la notificación. Incrementa el contador de intentos y,
     * según el resultado, deja la notificación en ENVIADA o FALLIDA. Si el
     * envío falla, relanza {@link EnvioNotificacionException} tras persistir
     * el estado FALLIDA.
     */
    public Notificacion enviar(Notificacion notificacion) {
        notificacion.setIntentos(notificacion.getIntentos() + 1);
        try {
            telegramPort.enviar(notificacion.getDestinatario(), notificacion.getMensaje());
            notificacion.setEstado(EstadoNotificacion.ENVIADA);
            notificacion.setFechaEnvio(LocalDateTime.now());
        } catch (EnvioNotificacionException e) {
            notificacion.setEstado(EstadoNotificacion.FALLIDA);
            repository.guardar(notificacion);
            throw e;
        }
        return repository.guardar(notificacion);
    }
}
