package com.uber.pagos.domain.usecase;

import com.uber.pagos.domain.exception.PagoNoEncontradoException;
import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.gateway.PagoGateway;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Caso de uso CRUD base del agregado Pago.
 */
@RequiredArgsConstructor
public class PagoCrudUseCase {

    private final PagoGateway pagoGateway;

    public Pago crear(Pago pago) {
        if (pago.getMoneda() == null || pago.getMoneda().isBlank()) {
            pago.setMoneda("USD");
        }
        if (pago.getEstado() == null) {
            pago.setEstado(EstadoPago.PENDIENTE);
        }
        if (pago.getFecha() == null) {
            pago.setFecha(LocalDateTime.now());
        }
        return pagoGateway.guardar(pago);
    }

    public Pago buscarPorId(Long id) {
        return pagoGateway.buscarPorId(id)
                .orElseThrow(() -> new PagoNoEncontradoException(id));
    }

    public List<Pago> listar() {
        return pagoGateway.listar();
    }

    public Pago actualizar(Long id, Pago datos) {
        Pago existente = buscarPorId(id);
        if (datos.getMonto() != null) {
            existente.setMonto(datos.getMonto());
        }
        if (datos.getMoneda() != null) {
            existente.setMoneda(datos.getMoneda());
        }
        if (datos.getMetodoPago() != null) {
            existente.setMetodoPago(datos.getMetodoPago());
        }
        if (datos.getEstado() != null) {
            existente.setEstado(datos.getEstado());
        }
        return pagoGateway.guardar(existente);
    }

    public void eliminar(Long id) {
        if (pagoGateway.buscarPorId(id).isEmpty()) {
            throw new PagoNoEncontradoException(id);
        }
        pagoGateway.eliminar(id);
    }
}
