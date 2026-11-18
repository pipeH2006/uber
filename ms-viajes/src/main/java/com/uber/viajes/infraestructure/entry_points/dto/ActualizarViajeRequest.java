package com.uber.viajes.infraestructure.entry_points.dto;

import java.math.BigDecimal;

public record ActualizarViajeRequest(
        Long pasajeroId,
        Long conductorId,
        String origenDireccion,
        String destinoDireccion,
        BigDecimal costoEstimado,
        BigDecimal costoFinal,
        String moneda
) {
}
