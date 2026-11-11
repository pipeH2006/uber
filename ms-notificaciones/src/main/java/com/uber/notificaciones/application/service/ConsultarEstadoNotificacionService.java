package com.uber.notificaciones.application.service;

import com.uber.notificaciones.application.port.in.ConsultarEstadoNotificacionUseCase;
import com.uber.notificaciones.application.port.out.NotificacionRepositoryPort;
import com.uber.notificaciones.domain.exception.NotificacionNoEncontradaException;
import com.uber.notificaciones.domain.model.Notificacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsultarEstadoNotificacionService implements ConsultarEstadoNotificacionUseCase {

    private final NotificacionRepositoryPort repository;

    @Override
    public Notificacion estado(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new NotificacionNoEncontradaException(id));
    }
}
