package com.uber.viajes.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ViajeJpaRepository extends JpaRepository<ViajeJpaEntity, Long> {
}
