package com.uber.usuarios.domain.usecase;

import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.Calificacion;
import com.uber.usuarios.domain.model.CalificacionResumen;
import com.uber.usuarios.domain.model.gateway.CalificacionGateway;
import com.uber.usuarios.domain.model.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Caso de uso para obtener el promedio de calificaciones de un usuario.
 */
@RequiredArgsConstructor
public class ObtenerCalificacionPromedioUseCase {

    private final UsuarioGateway usuarioGateway;
    private final CalificacionGateway calificacionGateway;

    public CalificacionResumen promedio(Long usuarioId) {
        usuarioGateway.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));

        List<Calificacion> calificaciones = calificacionGateway.buscarPorUsuarioId(usuarioId);
        if (calificaciones.isEmpty()) {
            return new CalificacionResumen(0.0, 0);
        }
        double suma = calificaciones.stream()
                .mapToInt(Calificacion::getPuntaje)
                .sum();
        return new CalificacionResumen(suma / calificaciones.size(), calificaciones.size());
    }
}
