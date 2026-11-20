package com.uber.viajes.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record Estimacion(
        double distanciaKm,
        double duracionMin,
        BigDecimal costoUSD,
        BigDecimal costoCOP,
        BigDecimal tipoCambio,
        List<Coordenada> ruta
) {
    public Estimacion(double distanciaKm, double duracionMin, BigDecimal costoUSD, BigDecimal costoCOP,
                      BigDecimal tipoCambio) {
        this(distanciaKm, duracionMin, costoUSD, costoCOP, tipoCambio, List.of());
    }
}
