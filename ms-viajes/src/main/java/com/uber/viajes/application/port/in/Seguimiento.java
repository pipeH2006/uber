package com.uber.viajes.application.port.in;

import com.uber.viajes.domain.model.EstadoViaje;

import java.math.BigDecimal;

public record Seguimiento(
        Long viajeId,
        EstadoViaje estado,
        double distanciaKm,
        double duracionMin,
        BigDecimal costoEstimado
) {
}
