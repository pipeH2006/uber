package com.uber.pagos.infrastructure.adapter.in.rest.dto;

import java.math.BigDecimal;

public record TipoCambioResponse(String moneda, BigDecimal tipoCambio) {
}
