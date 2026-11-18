package com.uber.pagos.infraestructure.entry_points.dto;

import java.math.BigDecimal;

public record TipoCambioResponse(String moneda, BigDecimal tipoCambio) {
}
