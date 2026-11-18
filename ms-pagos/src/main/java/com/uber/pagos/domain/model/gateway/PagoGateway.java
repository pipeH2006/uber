package com.uber.pagos.domain.model.gateway;

import com.uber.pagos.domain.model.Pago;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (gateway) hacia la persistencia de pagos.
 */
public interface PagoGateway {

    Pago guardar(Pago pago);

    Optional<Pago> buscarPorId(Long id);

    List<Pago> listar();

    void eliminar(Long id);

    List<Pago> listarAprobadosPorFecha(LocalDate fecha);
}
