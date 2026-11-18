package com.uber.pagos.infraestructure.driver_adapters.jpa_repository;

import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.gateway.PagoGateway;
import com.uber.pagos.infraestructure.mapper.MapperPago;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador que implementa el gateway de salida {@link PagoGateway}
 * delegando en Spring Data JPA.
 */
@Repository
@RequiredArgsConstructor
public class PagoDataGatewayImpl implements PagoGateway {

    private final PagoDataJpaRepository repository;
    private final MapperPago mapperPago;

    @Override
    public Pago guardar(Pago pago) {
        return mapperPago.toPago(repository.save(mapperPago.toData(pago)));
    }

    @Override
    public Optional<Pago> buscarPorId(Long id) {
        return repository.findById(id).map(mapperPago::toPago);
    }

    @Override
    public List<Pago> listar() {
        return repository.findAll().stream().map(mapperPago::toPago).toList();
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Pago> listarAprobadosPorFecha(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        return repository.findByEstadoAndFechaBetween(EstadoPago.APROBADO, inicio, fin).stream()
                .map(mapperPago::toPago)
                .toList();
    }
}
