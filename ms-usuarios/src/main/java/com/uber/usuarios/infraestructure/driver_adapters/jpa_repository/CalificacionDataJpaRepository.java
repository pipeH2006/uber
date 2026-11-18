package com.uber.usuarios.infraestructure.driver_adapters.jpa_repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalificacionDataJpaRepository extends JpaRepository<CalificacionData, Long> {

    List<CalificacionData> findByUsuarioId(Long usuarioId);
}
