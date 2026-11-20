package com.uber.pagos.infraestructure.driver_adapters.jpa_repository;

import com.uber.pagos.domain.model.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PagoDataJpaRepository extends JpaRepository<PagoData, Long> {
    List<PagoData> findByEstadoAndFechaBetween(EstadoPago estado, LocalDateTime inicio, LocalDateTime fin);
}
