package com.uber.usuarios.domain.model.gateway;

import com.uber.usuarios.domain.model.Calificacion;

import java.util.List;

public interface CalificacionGateway {
    Calificacion guardar(Calificacion calificacion);

    List<Calificacion> buscarPorUsuarioId(Long usuarioId);
}
