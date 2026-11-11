package com.uber.notificaciones.application.service;

import com.uber.notificaciones.application.port.in.EnviarNotificacionUseCase;
import com.uber.notificaciones.application.port.out.NotificacionRepositoryPort;
import com.uber.notificaciones.domain.model.Canal;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.TipoNotificacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnviarNotificacionService implements EnviarNotificacionUseCase {

    private final NotificacionRepositoryPort repository;
    private final NotificacionSender sender;

    @Override
    public Notificacion enviar(String destinatario, String mensaje, TipoNotificacion tipo) {
        Notificacion notificacion = Notificacion.builder()
                .destinatario(destinatario)
                .canal(Canal.TELEGRAM)
                .tipo(tipo == null ? TipoNotificacion.GENERICO : tipo)
                .mensaje(mensaje)
                .estado(EstadoNotificacion.PENDIENTE)
                .intentos(0)
                .build();
        Notificacion guardada = repository.guardar(notificacion);
        return sender.enviar(guardada);
    }
}
