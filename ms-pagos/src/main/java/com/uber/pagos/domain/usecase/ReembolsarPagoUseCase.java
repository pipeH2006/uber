package com.uber.pagos.domain.usecase;

import com.uber.pagos.domain.exception.EstadoPagoInvalidoException;
import com.uber.pagos.domain.exception.PagoNoEncontradoException;
import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.gateway.PagoGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReembolsarPagoUseCase {
    private final PagoGateway pagoGateway;

    public Pago reembolsar(Long id) {
        Pago pago = pagoGateway.buscarPorId(id)
                .orElseThrow(() -> new PagoNoEncontradoException(id));

        if (pago.getEstado() != EstadoPago.APROBADO) {
            throw new EstadoPagoInvalidoException(
                    "Solo se puede reembolsar un pago en estado APROBADO, estado actual: " + pago.getEstado());
        }
        pago.setEstado(EstadoPago.REEMBOLSADO);
        return pagoGateway.guardar(pago);
    }
}
