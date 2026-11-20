package com.uber.pagos.domain.model.gateway;

import com.uber.pagos.domain.model.Pago;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PagoGateway {
    Pago guardar(Pago pago);

    Optional<Pago> buscarPorId(Long id);

    List<Pago> listar();

    void eliminar(Long id);

    List<Pago> listarAprobadosPorFecha(LocalDate fecha);
}
