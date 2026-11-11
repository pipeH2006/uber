package com.uber.notificaciones.application.port.out;

import com.uber.notificaciones.domain.model.Notificacion;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (driven port) hacia la persistencia del agregado
 * Notificacion.
 */
public interface NotificacionRepositoryPort {

    Notificacion guardar(Notificacion notificacion);

    Optional<Notificacion> buscarPorId(Long id);

    List<Notificacion> listar();

    void eliminar(Long id);
}
