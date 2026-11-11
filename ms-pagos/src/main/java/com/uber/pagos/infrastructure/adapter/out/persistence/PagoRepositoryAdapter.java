package com.uber.pagos.infrastructure.adapter.out.persistence;

import com.uber.pagos.application.port.out.PagoRepositoryPort;
import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.Pago;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador que implementa el puerto de salida {@link PagoRepositoryPort}
 * delegando en Spring Data JPA.
 */
@Component
@RequiredArgsConstructor
public class PagoRepositoryAdapter implements PagoRepositoryPort {

    private final PagoJpaRepository jpaRepository;

    @Override
    public Pago guardar(Pago pago) {
        return toDomain(jpaRepository.save(toEntity(pago)));
    }

    @Override
    public Optional<Pago> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Pago> listar() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Pago> listarAprobadosPorFecha(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        return jpaRepository.findByEstadoAndFechaBetween(EstadoPago.APROBADO, inicio, fin).stream()
                .map(this::toDomain)
                .toList();
    }

    private PagoJpaEntity toEntity(Pago p) {
        return PagoJpaEntity.builder()
                .id(p.getId())
                .viajeId(p.getViajeId())
                .usuarioId(p.getUsuarioId())
                .monto(p.getMonto())
                .moneda(p.getMoneda())
                .metodoPago(p.getMetodoPago())
                .estado(p.getEstado())
                .fecha(p.getFecha())
                .referencia(p.getReferencia())
                .riesgoFraude(p.getRiesgoFraude())
                .build();
    }

    private Pago toDomain(PagoJpaEntity e) {
        return Pago.builder()
                .id(e.getId())
                .viajeId(e.getViajeId())
                .usuarioId(e.getUsuarioId())
                .monto(e.getMonto())
                .moneda(e.getMoneda())
                .metodoPago(e.getMetodoPago())
                .estado(e.getEstado())
                .fecha(e.getFecha())
                .referencia(e.getReferencia())
                .riesgoFraude(e.getRiesgoFraude())
                .build();
    }
}
