package com.uber.viajes.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record Seguimiento(
        Long viajeId,
        EstadoViaje estado,
        double distanciaKm,
        double duracionMin,
        BigDecimal costoEstimado,
        List<Coordenada> ruta
) {

    public Seguimiento(Long viajeId, EstadoViaje estado, double distanciaKm, double duracionMin,
                       BigDecimal costoEstimado) {
        this(viajeId, estado, distanciaKm, duracionMin, costoEstimado, List.of());
    }
}
