package com.uber.notificaciones.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionJpaRepository extends JpaRepository<NotificacionJpaEntity, Long> {
}
