package com.uber.notificaciones.application.port.in;

import com.uber.notificaciones.domain.model.Notificacion;

import java.util.List;

/**
 * Casos de uso CRUD base del agregado Notificacion.
 */
public interface NotificacionCrudUseCase {

    Notificacion crear(Notificacion notificacion);

    Notificacion buscarPorId(Long id);

    List<Notificacion> listar();

    Notificacion actualizar(Long id, Notificacion datos);

    void eliminar(Long id);
}
