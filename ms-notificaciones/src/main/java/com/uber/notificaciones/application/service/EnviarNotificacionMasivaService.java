package com.uber.notificaciones.application.service;

import com.uber.notificaciones.application.port.in.EnviarNotificacionMasivaUseCase;
import com.uber.notificaciones.application.port.out.NotificacionRepositoryPort;
import com.uber.notificaciones.domain.exception.EnvioNotificacionException;
import com.uber.notificaciones.domain.model.Canal;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.TipoNotificacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnviarNotificacionMasivaService implements EnviarNotificacionMasivaUseCase {

    private final NotificacionRepositoryPort repository;
    private final NotificacionSender sender;

    @Override
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
            Notificacion guardada = repository.guardar(notificacion);
            try {
                resultados.add(sender.enviar(guardada));
            } catch (EnvioNotificacionException e) {
                resultados.add(guardada);
            }
        }
        return resultados;
    }
}
