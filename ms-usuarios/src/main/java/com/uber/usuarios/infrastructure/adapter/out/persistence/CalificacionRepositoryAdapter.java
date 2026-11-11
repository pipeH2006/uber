package com.uber.usuarios.infrastructure.adapter.out.persistence;

import com.uber.usuarios.application.port.out.CalificacionRepositoryPort;
import com.uber.usuarios.domain.model.Calificacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CalificacionRepositoryAdapter implements CalificacionRepositoryPort {

    private final CalificacionJpaRepository jpaRepository;

    @Override
    public Calificacion guardar(Calificacion calificacion) {
        return toDomain(jpaRepository.save(toEntity(calificacion)));
    }

    @Override
    public List<Calificacion> buscarPorUsuarioId(Long usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toDomain)
                .toList();
    }

    private CalificacionJpaEntity toEntity(Calificacion c) {
        return CalificacionJpaEntity.builder()
                .id(c.getId())
                .usuarioId(c.getUsuarioId())
                .calificadorId(c.getCalificadorId())
                .puntaje(c.getPuntaje())
                .comentario(c.getComentario())
                .fecha(c.getFecha())
                .build();
    }

    private Calificacion toDomain(CalificacionJpaEntity e) {
        return Calificacion.builder()
                .id(e.getId())
                .usuarioId(e.getUsuarioId())
                .calificadorId(e.getCalificadorId())
                .puntaje(e.getPuntaje())
                .comentario(e.getComentario())
                .fecha(e.getFecha())
                .build();
    }
}
