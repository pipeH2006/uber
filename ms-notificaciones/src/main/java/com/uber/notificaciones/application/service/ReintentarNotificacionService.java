package com.uber.notificaciones.application.service;

import com.uber.notificaciones.application.port.in.ReintentarNotificacionUseCase;
import com.uber.notificaciones.application.port.out.NotificacionRepositoryPort;
import com.uber.notificaciones.domain.exception.EstadoInvalidoException;
import com.uber.notificaciones.domain.exception.NotificacionNoEncontradaException;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.Notificacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReintentarNotificacionService implements ReintentarNotificacionUseCase {

    private final NotificacionRepositoryPort repository;
    private final NotificacionSender sender;

    @Override
    public Notificacion reintentar(Long id) {
        Notificacion notificacion = repository.buscarPorId(id)
                .orElseThrow(() -> new NotificacionNoEncontradaException(id));
        if (notificacion.getEstado() != EstadoNotificacion.FALLIDA) {
            throw new EstadoInvalidoException("Solo se puede reintentar una notificación en estado FALLIDA");
        }
        return sender.enviar(notificacion);
    }
}
