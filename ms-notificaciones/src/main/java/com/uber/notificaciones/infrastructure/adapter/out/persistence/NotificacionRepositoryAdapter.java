package com.uber.notificaciones.infrastructure.adapter.out.persistence;

import com.uber.notificaciones.application.port.out.NotificacionRepositoryPort;
import com.uber.notificaciones.domain.model.Notificacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador que implementa el puerto de salida {@link NotificacionRepositoryPort}
 * delegando en Spring Data JPA.
 */
@Component
@RequiredArgsConstructor
public class NotificacionRepositoryAdapter implements NotificacionRepositoryPort {

    private final NotificacionJpaRepository jpaRepository;

    @Override
    public Notificacion guardar(Notificacion notificacion) {
        return toDomain(jpaRepository.save(toEntity(notificacion)));
    }

    @Override
    public Optional<Notificacion> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Notificacion> listar() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }

    private NotificacionJpaEntity toEntity(Notificacion n) {
        return NotificacionJpaEntity.builder()
                .id(n.getId())
                .destinatario(n.getDestinatario())
                .canal(n.getCanal())
                .tipo(n.getTipo())
                .mensaje(n.getMensaje())
                .estado(n.getEstado())
                .fechaEnvio(n.getFechaEnvio())
                .intentos(n.getIntentos())
                .build();
    }

    private Notificacion toDomain(NotificacionJpaEntity e) {
        return Notificacion.builder()
                .id(e.getId())
                .destinatario(e.getDestinatario())
                .canal(e.getCanal())
                .tipo(e.getTipo())
                .mensaje(e.getMensaje())
                .estado(e.getEstado())
                .fechaEnvio(e.getFechaEnvio())
                .intentos(e.getIntentos())
                .build();
    }
}
