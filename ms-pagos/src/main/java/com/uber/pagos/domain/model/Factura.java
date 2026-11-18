package com.uber.pagos.domain.model;

import java.math.BigDecimal;

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
