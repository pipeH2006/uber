package com.uber.viajes.domain.model;

import java.math.BigDecimal;

public record Seguimiento(
        Long viajeId,
        EstadoViaje estado,
        double distanciaKm,
        double duracionMin,
        BigDecimal costoEstimado
) {
}
