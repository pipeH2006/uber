package com.uber.usuarios.domain.usecase;

import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.Calificacion;
import com.uber.usuarios.domain.model.Usuario;
import com.uber.usuarios.domain.model.gateway.CalificacionGateway;
import com.uber.usuarios.domain.model.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CalificarUsuarioUseCase {
    private final UsuarioGateway usuarioGateway;
    private final CalificacionGateway calificacionGateway;

    public Usuario calificar(Long usuarioId, Long calificadorId, Integer puntaje, String comentario) {
        if (puntaje == null || puntaje < 1 || puntaje > 5) {
            throw new IllegalArgumentException("El puntaje debe estar entre 1 y 5");
        }
        usuarioGateway.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));
        usuarioGateway.buscarPorId(calificadorId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(calificadorId));

        Calificacion calificacion = Calificacion.builder()
                .usuarioId(usuarioId)
                .calificadorId(calificadorId)
                .puntaje(puntaje)
                .comentario(comentario)
                .fecha(LocalDateTime.now())
                .build();

        calificacionGateway.guardar(calificacion);
        return usuarioGateway.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));
    }
}
