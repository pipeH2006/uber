package com.uber.notificaciones.infrastructure.adapter.out.persistence;

import com.uber.notificaciones.application.port.out.PlantillaRepositoryPort;
import com.uber.notificaciones.domain.model.Plantilla;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador que implementa el puerto de salida {@link PlantillaRepositoryPort}
 * delegando en Spring Data JPA.
 */
@Component
@RequiredArgsConstructor
public class PlantillaRepositoryAdapter implements PlantillaRepositoryPort {

    private final PlantillaJpaRepository jpaRepository;

    @Override
    public Plantilla guardar(Plantilla plantilla) {
        return toDomain(jpaRepository.save(toEntity(plantilla)));
    }

    @Override
    public Optional<Plantilla> buscarPorCodigo(String codigo) {
        return jpaRepository.findByCodigo(codigo).map(this::toDomain);
    }

    @Override
    public List<Plantilla> listar() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    private PlantillaJpaEntity toEntity(Plantilla p) {
        return PlantillaJpaEntity.builder()
                .id(p.getId())
                .codigo(p.getCodigo())
                .asunto(p.getAsunto())
                .mensaje(p.getMensaje())
                .build();
    }

    private Plantilla toDomain(PlantillaJpaEntity e) {
        return Plantilla.builder()
                .id(e.getId())
                .codigo(e.getCodigo())
                .asunto(e.getAsunto())
                .mensaje(e.getMensaje())
                .build();
    }
}
