package com.uber.viajes.infraestructure.driver_adapters.jpa_repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ViajeDataJpaRepository extends JpaRepository<ViajeData, Long> {
}
