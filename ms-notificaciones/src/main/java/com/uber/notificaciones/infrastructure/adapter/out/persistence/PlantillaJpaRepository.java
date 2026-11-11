package com.uber.notificaciones.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlantillaJpaRepository extends JpaRepository<PlantillaJpaEntity, Long> {

    Optional<PlantillaJpaEntity> findByCodigo(String codigo);
}
