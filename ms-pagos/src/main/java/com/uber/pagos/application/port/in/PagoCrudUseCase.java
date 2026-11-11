package com.uber.pagos.application.port.in;

import com.uber.pagos.domain.model.Pago;

import java.util.List;

/**
 * Casos de uso CRUD base del agregado Pago.
 */
public interface PagoCrudUseCase {

    Pago crear(Pago pago);

    Pago buscarPorId(Long id);

    List<Pago> listar();

    Pago actualizar(Long id, Pago datos);

    void eliminar(Long id);
}
