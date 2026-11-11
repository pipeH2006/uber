package com.uber.usuarios.application.port.out;

import com.uber.usuarios.domain.model.Calificacion;

import java.util.List;

/**
 * Puerto de salida para la persistencia de calificaciones.
 */
public interface CalificacionRepositoryPort {

    Calificacion guardar(Calificacion calificacion);

    List<Calificacion> buscarPorUsuarioId(Long usuarioId);
}
