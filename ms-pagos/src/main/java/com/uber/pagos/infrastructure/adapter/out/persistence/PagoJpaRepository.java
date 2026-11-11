package com.uber.pagos.infrastructure.adapter.out.persistence;

import com.uber.pagos.domain.model.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PagoJpaRepository extends JpaRepository<PagoJpaEntity, Long> {

    List<PagoJpaEntity> findByEstadoAndFechaBetween(EstadoPago estado, LocalDateTime inicio, LocalDateTime fin);
}
