package com.uber.notificaciones.domain.usecase;

import com.uber.notificaciones.domain.exception.EnvioNotificacionException;
import com.uber.notificaciones.domain.model.Canal;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.TipoNotificacion;
import com.uber.notificaciones.domain.model.gateway.NotificacionGateway;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Caso de uso que envía una notificación a múltiples destinatarios.
 * El fallo de un destinatario no aborta el envío del resto.
 */
@RequiredArgsConstructor
public class EnviarNotificacionMasivaUseCase {

    private final NotificacionGateway notificacionGateway;
    private final NotificacionSender sender;

    public List<Notificacion> enviarMasivo(List<String> destinatarios, String mensaje, TipoNotificacion tipo) {
        List<Notificacion> resultados = new ArrayList<>();
        for (String destinatario : destinatarios) {
            Notificacion notificacion = Notificacion.builder()
                    .destinatario(destinatario)
                    .canal(Canal.TELEGRAM)
                    .tipo(tipo == null ? TipoNotificacion.GENERICO : tipo)
                    .mensaje(mensaje)
                    .estado(EstadoNotificacion.PENDIENTE)
                    .intentos(0)
                    .build();
            Notificacion guardada = notificacionGateway.guardar(notificacion);
            try {
                resultados.add(sender.enviar(guardada));
            } catch (EnvioNotificacionException e) {
                resultados.add(guardada);
            }
        }
        return resultados;
    }
}
