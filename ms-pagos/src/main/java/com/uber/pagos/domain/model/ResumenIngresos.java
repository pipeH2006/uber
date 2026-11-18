package com.uber.pagos.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ResumenIngresos(LocalDate fecha, BigDecimal totalUSD, long cantidadPagos) {
}
