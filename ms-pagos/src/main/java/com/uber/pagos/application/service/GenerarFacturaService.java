package com.uber.pagos.application.service;

import com.uber.pagos.application.port.in.Factura;
import com.uber.pagos.application.port.in.GenerarFacturaUseCase;
import com.uber.pagos.application.port.out.PagoRepositoryPort;
import com.uber.pagos.application.port.out.TipoCambioPort;
import com.uber.pagos.domain.exception.PagoNoEncontradoException;
import com.uber.pagos.domain.model.Pago;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class GenerarFacturaService implements GenerarFacturaUseCase {

    private final PagoRepositoryPort repository;
    private final TipoCambioPort tipoCambioPort;

    @Override
    public Factura generarFactura(Long id) {
        Pago pago = repository.buscarPorId(id)
                .orElseThrow(() -> new PagoNoEncontradoException(id));

        BigDecimal tipoCambio = tipoCambioPort.obtenerTipoCambio("COP");
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
