package com.uber.viajes.application.port.in;

import java.math.BigDecimal;

public record Estimacion(
        double distanciaKm,
        double duracionMin,
        BigDecimal costoUSD,
        BigDecimal costoCOP,
        BigDecimal tipoCambio
) {
}
