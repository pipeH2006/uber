package com.uber.pagos.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ResumenIngresos(LocalDate fecha, BigDecimal totalUSD, long cantidadPagos) {
}
