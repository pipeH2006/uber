package com.uber.pagos.domain.usecase;

import com.uber.pagos.domain.exception.PagoNoEncontradoException;
import com.uber.pagos.domain.model.Factura;
import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.gateway.PagoGateway;
import com.uber.pagos.domain.model.gateway.TipoCambioGateway;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class GenerarFacturaUseCase {
    private final PagoGateway pagoGateway;
    private final TipoCambioGateway tipoCambioGateway;

    public Factura generarFactura(Long id) {
        Pago pago = pagoGateway.buscarPorId(id)
                .orElseThrow(() -> new PagoNoEncontradoException(id));

        BigDecimal tipoCambio = tipoCambioGateway.obtenerTipoCambio("COP");
        BigDecimal montoCOP = pago.getMonto().multiply(tipoCambio);

        return new Factura(
                pago.getId(),
                pago.getViajeId(),
                pago.getMonto(),
                pago.getMoneda(),
                montoCOP,
                tipoCambio,
                pago.getEstado(),
                pago.getReferencia()
        );
    }
}
