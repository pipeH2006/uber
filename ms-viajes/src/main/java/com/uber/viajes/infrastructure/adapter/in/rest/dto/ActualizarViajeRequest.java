package com.uber.viajes.infrastructure.adapter.in.rest.dto;

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
