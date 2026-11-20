package com.uber.notificaciones.domain.model.gateway;

import com.uber.notificaciones.domain.model.Notificacion;

import java.util.List;
import java.util.Optional;

public interface NotificacionGateway {
    Notificacion guardar(Notificacion notificacion);

    Optional<Notificacion> buscarPorId(Long id);

    List<Notificacion> listar();

    void eliminar(Long id);
}
