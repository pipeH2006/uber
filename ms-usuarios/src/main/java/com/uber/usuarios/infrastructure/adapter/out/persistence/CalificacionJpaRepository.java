package com.uber.usuarios.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalificacionJpaRepository extends JpaRepository<CalificacionJpaEntity, Long> {

    List<CalificacionJpaEntity> findByUsuarioId(Long usuarioId);
}
