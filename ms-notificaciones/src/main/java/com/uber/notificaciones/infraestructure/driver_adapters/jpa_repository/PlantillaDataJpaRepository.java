package com.uber.notificaciones.infraestructure.driver_adapters.jpa_repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlantillaDataJpaRepository extends JpaRepository<PlantillaData, Long> {

    Optional<PlantillaData> findByCodigo(String codigo);
}
