package com.uber.usuarios.application.service;

import com.uber.usuarios.application.port.in.CalificacionResumen;
import com.uber.usuarios.application.port.in.CalificarUsuarioUseCase;
import com.uber.usuarios.application.port.in.ObtenerCalificacionPromedioUseCase;
import com.uber.usuarios.application.port.out.CalificacionRepositoryPort;
import com.uber.usuarios.application.port.out.UsuarioRepositoryPort;
import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.Calificacion;
import com.uber.usuarios.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalificacionService implements CalificarUsuarioUseCase, ObtenerCalificacionPromedioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final CalificacionRepositoryPort calificacionRepository;

    @Override
    public Usuario calificar(Long usuarioId, Long calificadorId, Integer puntaje, String comentario) {
        if (puntaje == null || puntaje < 1 || puntaje > 5) {
            throw new IllegalArgumentException("El puntaje debe estar entre 1 y 5");
        }
        usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));
        usuarioRepository.buscarPorId(calificadorId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(calificadorId));

        Calificacion calificacion = Calificacion.builder()
                .usuarioId(usuarioId)
                .calificadorId(calificadorId)
                .puntaje(puntaje)
                .comentario(comentario)
                .fecha(LocalDateTime.now())
                .build();

        calificacionRepository.guardar(calificacion);
        return usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));
    }

    @Override
    public CalificacionResumen promedio(Long usuarioId) {
        usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));

        List<Calificacion> calificaciones = calificacionRepository.buscarPorUsuarioId(usuarioId);
        if (calificaciones.isEmpty()) {
            return new CalificacionResumen(0.0, 0);
        }
        double suma = calificaciones.stream()
                .mapToInt(Calificacion::getPuntaje)
                .sum();
        return new CalificacionResumen(suma / calificaciones.size(), calificaciones.size());
    }
}
