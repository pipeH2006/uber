package com.uber.pagos.application.port.in;

import java.math.BigDecimal;

import com.uber.pagos.domain.model.EstadoPago;

public record Factura(
        Long pagoId,
        Long viajeId,
        BigDecimal monto,
        String moneda,
        BigDecimal montoCOP,
        BigDecimal tipoCambio,
        EstadoPago estado,
        String referencia
) {
}
